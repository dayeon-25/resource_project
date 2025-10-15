package com.example.resource.repository;

import com.example.resource.entity.AnalysisImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnalysisImageRepository extends JpaRepository<AnalysisImage, Long> {
    List<AnalysisImage> findAllByOrigImgId(Long origImgId);
    Optional<AnalysisImage> findAllByOrigImgIdAndType(Long origImageId, Integer type);
}
