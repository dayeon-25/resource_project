package com.example.resource.service;

import com.example.resource.dto.AnalysisResponse;
import com.example.resource.entity.Member;
import com.example.resource.exception.AnalysisFailedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ImageAnalysisService {

    private final AnalysisResultService analysisResultService;
    private final WebClient webClient;

    public ImageAnalysisService(AnalysisResultService analysisResultService,
                               WebClient imageClient) {

        this.analysisResultService = analysisResultService;
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

}