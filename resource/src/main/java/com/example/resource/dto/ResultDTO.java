package com.example.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResultDTO {
    private String analysisDate;
    private String origImage;
    private String rcnnResult;
    private String opencvPro;
    private String opencvResult;
    private String pca;
    private double plastic;
    private double vinyl;
    private double wood;
    private double avgPlastic;
    private double avgVinyl;
    private double avgWood;
    private double total;
    private String suitable;
}