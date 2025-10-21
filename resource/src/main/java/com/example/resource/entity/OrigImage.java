package com.example.resource.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(name = "orig_image")
public class OrigImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Lob
    @Column(name="image_data", columnDefinition="MEDIUMBLOB")
    private byte[] imageData;

    @Column(name="analysis_date")
    @Builder.Default
    private LocalDateTime analysisDate = LocalDateTime.now();

}