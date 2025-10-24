package com.example.resource.service;

import com.example.resource.dto.AnalysisResponse;
import com.example.resource.dto.MorphingEffect;
import com.example.resource.entity.Member;
import com.example.resource.entity.OrigImage;
import com.example.resource.exception.AnalysisFailedException;
import com.example.resource.repository.OrigImageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ImageAnalysisService {

    private final AnalysisResultService analysisResultService;
    private final OrigImageRepository origImageRepository;
    private final WebClient webClient;

    public ImageAnalysisService(AnalysisResultService analysisResultService, OrigImageRepository origImageRepository,
                                @Qualifier("imageClient") WebClient imageClient) {

        this.analysisResultService = analysisResultService;
        this.origImageRepository = origImageRepository;
        this.webClient = imageClient;
    }


    public Mono<Map<String, Object>> analyzeImage(MultipartFile image, Member member) {
        try {
            return sendToPythonServer(image)
                    .flatMap(response -> {
                        if (response.getStatus() != 0) {
                            log.error("Python 서버 응답 상태가 비정상적입니다. 상태: {}", response.getStatus());
                            return Mono.error(new AnalysisFailedException("status: " + response.getStatus()));
                        }

                        return Mono.fromFuture(() -> analysisResultService.saveAsync(image, member, response))
                                .map(origImgId -> {
                                    Map<String, Object> result = new HashMap<>();
                                    result.put("id", origImgId);
                                    result.put("status", 0);
                                    return result;
                                })
                                .onErrorResume(e -> {
                                    log.error("분석 결과 저장 중 오류 발생: {}", e.getMessage(), e);
                                    return Mono.error(new AnalysisFailedException(e.getMessage()));
                                });
                    });
        } catch (IOException e) {
            log.error("이미지 분석 중 IOException 발생: {}", e.getMessage(), e);
            return Mono.error(new AnalysisFailedException("IOException: " + e.getMessage()));
        }
    }

    public Mono<AnalysisResponse> sendToPythonServer(MultipartFile file) throws IOException {

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", new InputStreamResource(file.getInputStream()))
                .header("Content-Disposition", "form-data; name=file; filename=" + file.getOriginalFilename());

        return webClient.post()
                .uri("/image/analyze")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(AnalysisResponse.class);
    }

    public MorphingEffect getMorphingFromPython(Long id, Member member) {
        OrigImage origImage = origImageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("이미지를 찾을 수 없습니다. id=" + id));

        if (!origImage.getMember().equals(member)) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }

        try {
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            byte[] imageData = origImage.getImageData();
            if (imageData == null || imageData.length == 0) {
                throw new IllegalArgumentException("이미지 데이터가 존재하지 않습니다.");
            }

            ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
            builder.part("file", new InputStreamResource(bais))
                    .header("Content-Disposition", "form-data; name=file; filename=image.png"); // 파일명 임의 지정 가능

            return webClient.post()
                    .uri("/image/analyze")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(builder.build()))
                    .retrieve()
                    .bodyToMono(MorphingEffect.class)
                    .block();

        } catch (Exception e) {
            log.error("Python 서버 모핑 호출 오류 발생: {}", e.getMessage(), e);
            throw new AnalysisFailedException("Python 서버와 통신 실패");
        }
    }


}