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

    private boolean suitable;

    private int count;
}
