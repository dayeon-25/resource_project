package com.example.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultsDTO {
    // 원본, 분석일
    private Long origImgID;
    private String origImage;
    private LocalDateTime analysisDate;

    // 분석 결과
    private double glass;
    private double vinyl;
    private double wood;
    private boolean suitable;

    // 분석 이미지, base64
    private String rcnnResult;
    private String opencvPro;
    private String opencvResult;
    private String pca;

}