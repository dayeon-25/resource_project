package com.example.resource.controller;


import com.example.resource.dto.ResultDTO;
import com.example.resource.entity.AnalysisImg;
import com.example.resource.entity.AnalysisResult;
import com.example.resource.entity.OrigImage;
import com.example.resource.repository.AnalysisImgRepository;
import com.example.resource.repository.AnalysisResultRepository;
import com.example.resource.repository.OrigImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ResultController {
    private final AnalysisImgRepository analysisImgRepository;
    private final AnalysisResultRepository analysisResultRepository;
    private final OrigImageRepository origImageRepository;

    @GetMapping("/result/{id}")
    public String getResult(@PathVariable Long id, Model model) {
        OrigImage origImage = origImageRepository.findById(id).orElse(null);
        String oriBase64 = Base64.getEncoder().encodeToString(origImage.getImageData());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
        String analysisDate = origImage.getAnalysisDate().format(formatter);

        AnalysisResult result = analysisResultRepository.findByOrigImage_Id(origImage.getId()).orElse(null);

        AnalysisImg rcnnImg = analysisImgRepository.findByOrigImage_IdAndType(origImage.getId(),10).orElse(null);
        String rcnnBase64 = Base64.getEncoder().encodeToString(rcnnImg.getImageData());

        AnalysisImg opencvProImg = analysisImgRepository.findByOrigImage_IdAndType(origImage.getId(),20).orElse(null);
        String opencvProBase64 = Base64.getEncoder().encodeToString(opencvProImg.getImageData());

        AnalysisImg opencvResultImg = analysisImgRepository.findByOrigImage_IdAndType(origImage.getId(),25).orElse(null);
        String opencvResultBase64 = Base64.getEncoder().encodeToString(opencvResultImg.getImageData());

        AnalysisImg pcaImg = analysisImgRepository.findByOrigImage_IdAndType(origImage.getId(),30).orElse(null);
        String pcaBase64 = Base64.getEncoder().encodeToString(pcaImg.getImageData());

        double avgPlastic = analysisResultRepository.getPlasticAvg();
        double avgVinyl = analysisResultRepository.getVinylAvg();
        double avgWood = analysisResultRepository.getWoodAvg();

        double total = result.getWood()+ result.getPlastic() + result.getVinyl();
        String suitable = (result.getSuitable()) ? "적합" : "부적합";

        ResultDTO resultDTO = new ResultDTO(analysisDate,
                oriBase64,
                rcnnBase64,
                opencvProBase64,
                opencvResultBase64,
                pcaBase64,
                result.getPlastic(),
                result.getVinyl(),
                result.getWood(),
                avgPlastic,
                avgVinyl,
                avgWood,
                total,
                suitable);

        model.addAttribute("result", resultDTO);

        return "result";
    }

}
