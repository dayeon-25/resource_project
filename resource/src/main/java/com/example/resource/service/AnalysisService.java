package com.example.resource.service;

import com.example.resource.entity.AnalysisResult;
import com.example.resource.entity.Member;
import com.example.resource.entity.OrigImage;
import com.example.resource.repository.AnalysisResultRepository;
import com.example.resource.repository.OrigImageRepository;
import com.example.resource.util.MultipartInputStreamFileResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final OrigImageRepository origImageRepository;
    private final AnalysisResultRepository analysisResultRepository;

    private final WebClient webClient = WebClient.create("http://192.168.0.63:8000");

    public Mono<Long> saveAnalysisResultAndGetOrigImgId(MultipartFile image, Member member) {
        return Mono.fromCallable(() ->
                        new MultipartInputStreamFileResource(image.getInputStream(), image.getOriginalFilename())
                ).subscribeOn(Schedulers.boundedElastic())
                .flatMap(fileResource ->
                        webClient.post()
                                .uri("/image/analyze")
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .body(BodyInserters.fromMultipartData("file", fileResource))
                                .retrieve()
                                .bodyToMono(Map.class)
                )
                .flatMap(result -> {
                    log.info("✅ FastAPI 응답: {}", result);

                    Object statusObj = result.get("status");
                    int status = (statusObj instanceof Number) ? ((Number) statusObj).intValue() : -1;
                    if (status != 0) return Mono.error(new RuntimeException("이미지 분석 실패 (status=" + status + ")"));

                    double total = (Double) result.get("plastic") + (Double) result.get("vinyl") + (Double) result.get("wood");
                    total = Math.round(total * 100) / 100.0;
                    boolean suitable = total <= 1;

                    // blocking DB 호출은 boundedElastic 스케줄러 사용
                    return Mono.fromCallable(() -> {
                        // 원본 이미지 저장
                        OrigImage origImage = new OrigImage();
                        origImage.setImageData(image.getBytes());
                        origImage.setMember(member);
                        origImage.setAnalysisDate(LocalDateTime.now());
                        OrigImage savedOrigImage = origImageRepository.save(origImage);

                        // 분석 결과 저장
                        AnalysisResult analysisResult = new AnalysisResult();
                        analysisResult.setPca(Base64.getDecoder().decode((String) result.get("pca")));
                        analysisResult.setOpencvPro(Base64.getDecoder().decode((String) result.get("opencv_pro")));
                        analysisResult.setOpencvResult(Base64.getDecoder().decode((String) result.get("opencv_result")));
                        analysisResult.setRcnnResult(Base64.getDecoder().decode((String) result.get("rcnn_result")));
                        analysisResult.setCount((Integer) result.get("count"));
                        analysisResult.setPlastic((Double) result.get("plastic"));
                        analysisResult.setWood((Double) result.get("wood"));
                        analysisResult.setVinyl((Double) result.get("vinyl"));
                        analysisResult.setSuitable(suitable);
                        analysisResult.setOrigImgId(savedOrigImage.getId());

                        analysisResultRepository.save(analysisResult);

                        return savedOrigImage.getId();
                    }).subscribeOn(Schedulers.boundedElastic());
                });
    }
}





//package com.example.resource.service;
//
//import com.example.resource.entity.AnalysisResult;
//import com.example.resource.entity.Member;
//import com.example.resource.entity.OrigImage;
//import com.example.resource.repository.AnalysisResultRepository;
//import com.example.resource.repository.OrigImageRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Service;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.time.LocalDateTime;
//import java.util.Base64;
//import java.util.Map;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class AnalysisService {
//    private final OrigImageRepository origImageRepository;
//    private final AnalysisResultRepository analysisResultRepository;
//
//    private final RestTemplate restTemplate = new RestTemplate();
//
//    private final String fastApiUrl = "http://192.168.0.63:8000/image/analyze";
//
//    public Long saveAnalysisResultAndGetOrigImgId(MultipartFile image, Member member) throws IOException{
//        // 1. FastAPI 호출
//        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//        body.add("file", new com.example.resource.util.MultipartInputStreamFileResource(image.getInputStream(), image.getOriginalFilename()));
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//
//        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
//        Map<String, Object> result = restTemplate.postForEntity(fastApiUrl, requestEntity, Map.class).getBody();
//        log.info("✅ FastAPI 응답 요약 =========");
//        log.info("status: {}", result.get("status"));
//        log.info("plastic: {}", result.get("plastic"));
//        log.info("vinyl: {}", result.get("vinyl"));
//        log.info("wood: {}", result.get("wood"));
//        log.info("count: {}", result.get("count"));
//        log.info("opencv_pro: {}", ((String) result.get("opencv_pro")).length());
//        log.info("opencv_result: {}", ((String) result.get("opencv_result")).length());
//        log.info("rcnn_result 길이: {}", ((String) result.get("rcnn_result")).length());
//        log.info("pca 길이: {}", ((String) result.get("pca")).length());
//        log.info("=================================");
//
//
//
//        Object statusObj = result.get("status");
//        int status = (statusObj instanceof Number) ? ((Number) statusObj).intValue() : -1;
//        double total = (Double) result.get("plastic") + (Double) result.get("vinyl") + (Double) result.get("wood");
//        total = total=Math.round(total*100)/100.0;
//        boolean suitable = (total<=1)  ? true : false;
//
//
//        if (status == 0) {
//            // 2. 원본 이미지 저장
//            OrigImage origImage = new OrigImage();
//            origImage.setImageData(image.getBytes());
//            origImage.setMember(member);
//            origImage.setAnalysisDate(LocalDateTime.now());
//            OrigImage savedOrigImage = origImageRepository.save(origImage);
//
//            // 3. 분석 결과 저장
//            AnalysisResult analysisResult = new AnalysisResult();
//            analysisResult.setPca(Base64.getDecoder().decode((String) result.get("pca")));
//            analysisResult.setOpencvPro(Base64.getDecoder().decode((String) result.get("opencv_pro")));
//            analysisResult.setOpencvResult(Base64.getDecoder().decode((String) result.get("opencv_result")));
//            analysisResult.setRcnnResult(Base64.getDecoder().decode((String) result.get("rcnn_result")));
//            analysisResult.setCount((Integer) result.get("count"));
//            analysisResult.setPlastic((Double) result.get("plastic"));
//            analysisResult.setWood((Double) result.get("wood"));
//            analysisResult.setVinyl((Double) result.get("vinyl"));
//            analysisResult.setSuitable(suitable);
//            analysisResult.setOrigImgId(savedOrigImage.getId());
//
//            analysisResultRepository.save(analysisResult);
//
//            return savedOrigImage.getId();
//
//        }else{
//            throw new RuntimeException("이미지 분석 실패");
//        }
//    }
//}
