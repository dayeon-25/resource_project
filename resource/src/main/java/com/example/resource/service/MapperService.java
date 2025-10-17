package com.example.resource.service;

import com.example.resource.dto.RaUsageDTO;
import com.example.resource.dto.ResultDTO;
import com.example.resource.entity.AnalysisResult;
import com.example.resource.entity.OrigImage;
import com.example.resource.entity.RaUsage;
import com.example.resource.repository.AnalysisResultRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MapperService {

    private final AnalysisResultRepository analysisResultRepository;

    public MapperService(AnalysisResultRepository analysisResultRepository) {
        this.analysisResultRepository = analysisResultRepository;
    }

    public List<RaUsageDTO> toRaUsageDTO(List<RaUsage> data) {
        return data.stream()
                .map(raUsage -> RaUsageDTO.builder()
                        .year(raUsage.getYear())
                        .sales(raUsage.getSales())
                        .build())
                .collect(Collectors.toList());
    }

    public RaUsage toRaUsage(RaUsageDTO raUsageDTO) {
        return RaUsage.builder()
                .year(raUsageDTO.getYear())
                .sales(raUsageDTO.getSales())
                .build();
    }

    public ResultDTO toResultDTO(AnalysisResult result, OrigImage origImage) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
        String analysisDate = origImage.getAnalysisDate().format(formatter);
        String suitable = (result.isSuitable()) ? "적합" : "부적합";

        return ResultDTO.builder()
                .origImage(Base64.getEncoder().encodeToString(origImage.getImageData()))
                .analysisDate(analysisDate)
                .plastic(result.getPlastic())
                .vinyl(result.getVinyl())
                .wood(result.getWood())
                .total(result.getWood()+ result.getPlastic() + result.getVinyl())
                .suitable(suitable)
                .avgPlastic(analysisResultRepository.getPlasticAvg())
                .avgVinyl(analysisResultRepository.getVinylAvg())
                .avgWood(analysisResultRepository.getWoodAvg())
                .rcnnResult(Base64.getEncoder().encodeToString(result.getRcnnResult()))
                .opencvPro(Base64.getEncoder().encodeToString(result.getOpencvPro()))
                .opencvResult(Base64.getEncoder().encodeToString(result.getOpencvResult()))
                .pca(Base64.getEncoder().encodeToString(result.getPca()))
                .build();
    }


}