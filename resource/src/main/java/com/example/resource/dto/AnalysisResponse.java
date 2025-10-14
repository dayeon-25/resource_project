package com.example.resource.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisResponse {
    private int status;
    @JsonProperty("orig_img_id")
    private Long origImgId;

    @JsonProperty("analysis_result")
    private Long analysisResult;

    @JsonProperty("rcnn_result")
    private Long rcnnResult;
    @JsonProperty("opencv_pro")
    private Long opencvPro;
    @JsonProperty("opencv_result")
    private Long opencvResult;
    @JsonProperty("pca")
    private Long pca;
}