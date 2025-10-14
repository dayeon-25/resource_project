package com.example.resource.repository;

import com.example.resource.entity.AnalysisImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnalysisImageRepository extends JpaRepository<AnalysisImage, Long> {
    List<AnalysisImage> findAllByOrigImgId(Long origImgId);
}
