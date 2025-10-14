package com.example.resource.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(name = "analysis_image")
public class AnalysisImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "orig_img_id")
    private Long origImgId;

    private int type;

    @Column(name="image_data", columnDefinition="MEDIUMBLOB")
    private byte[] imageData;
}