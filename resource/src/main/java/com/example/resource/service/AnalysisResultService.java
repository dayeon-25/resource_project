package com.example.resource.service;

import com.example.resource.dto.AnalysisResponse;
import com.example.resource.dto.ResultDTO;
import com.example.resource.entity.AnalysisResult;
import com.example.resource.entity.Member;
import com.example.resource.entity.OrigImage;
import com.example.resource.exception.AnalysisFailedException;
import com.example.resource.repository.AnalysisResultRepository;
import com.example.resource.repository.OrigImageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class AnalysisResultService {

    private final OrigImageRepository origImageRepository;
    private final AnalysisResultRepository analysisResultRepository;
    private final MapperService mapperService;

    public AnalysisResultService(OrigImageRepository origImageRepository, AnalysisResultRepository analysisResultRepository, MapperService mapperService) {
        this.origImageRepository = origImageRepository;
        this.analysisResultRepository = analysisResultRepository;
        this.mapperService = mapperService;
    }


    @Async
    public CompletableFuture<Long> saveAsync(MultipartFile file, Member member, AnalysisResponse response) {
        try {
            Long id = saveOrigAndResults(file, member, response);
            return CompletableFuture.completedFuture(id);
        } catch (IOException e) {
            log.error("DB 저장 중 오류 발생: {}", e.getMessage(), e);
            throw new AnalysisFailedException("분석 결과 db 저장 실패 : " + e.getMessage());
        }
    }

    @Transactional
    public Long saveOrigAndResults(MultipartFile file, Member member, AnalysisResponse response) throws IOException {
        OrigImage origImage = OrigImage.builder()
                .analysisDate(LocalDateTime.now())
                .imageData(file.getBytes())
                .member(member)
                .build();

        Long id = origImageRepository.save(origImage).getId();

        AnalysisResult result = AnalysisResult.builder()
                .origImgId(id)
                .plastic(response.getPlastic())
                .wood(response.getWood())
                .vinyl(response.getVinyl())
                .suitable((response.getPlastic() + response.getVinyl() + response.getWood()) < 1.0)
                .count(response.getCount())
                .rcnnResult(Base64.getDecoder().decode(response.getRcnnResult()))
                .opencvPro(Base64.getDecoder().decode(response.getOpencvPro()))
                .opencvResult(Base64.getDecoder().decode(response.getOpencvResult()))
                .pca(Base64.getDecoder().decode(response.getPca()))
                .build();

        analysisResultRepository.save(result);
        return id;
    }


    public ResultDTO getResultDTO(OrigImage origImage) {
        AnalysisResult result = analysisResultRepository.findByOrigImgId(origImage.getId());
        if (result == null) {
            return null;
        }
        return mapperService.toResultDTO(result, origImage);
    }
}