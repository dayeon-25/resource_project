package com.example.resource.controller;

import com.example.resource.dto.ResultDTO;
import com.example.resource.entity.AnalysisResult;
import com.example.resource.entity.Member;
import com.example.resource.entity.OrigImage;
import com.example.resource.repository.AnalysisResultRepository;
import com.example.resource.repository.OrigImageRepository;
import com.example.resource.service.MapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class ResultRestController {

    private final OrigImageRepository origImageRepository;
    private final AnalysisResultRepository analysisResultRepository;
    private final MapperService mapperService;

    @GetMapping("/results")
    public List<ResultDTO> getAllResults(Principal principal) {
        // 1. 로그인한 사용자의 이미지 목록 조회
        List<OrigImage> origImages = origImageRepository.findAll()
                .stream()
                .filter(img -> img.getMember().getUsername().equals(principal.getName()))
                .sorted((a, b) -> b.getAnalysisDate().compareTo(a.getAnalysisDate())) // 최신순
                .collect(Collectors.toList());

        // 2. 각 이미지에 대한 분석 결과 가져와 DTO 변환
        return origImages.stream()
                .map(img -> {
                    AnalysisResult result = analysisResultRepository.findByOrigImgId(img.getId());
                    return mapperService.toResultDTO(result, img);
                })
                .collect(Collectors.toList());
    }
}
