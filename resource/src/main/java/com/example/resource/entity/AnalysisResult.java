package com.example.resource.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(name = "analysis_result")
public class AnalysisResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "orig_img_id")
    private Long origImgId;

    private double plastic;
    private double vinyl;
    private double wood;

    private int count;
    private boolean suitable;

    @Lob
    @Column(name = "rcnn_result")
    private byte[] rcnnResult;

    @Lob
    @Column(name = "opencv_pro")
    private byte[] opencvPro;

    @Lob
    @Column(name = "opencv_result")
    private byte[] opencvResult;

    @Lob
    @Column(name = "pca")
    private byte[] pca;
}
