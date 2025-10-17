package com.example.resource.controller;


import com.example.resource.dto.ResultDTO;
import com.example.resource.entity.AnalysisResult;
import com.example.resource.entity.OrigImage;
import com.example.resource.repository.AnalysisResultRepository;
import com.example.resource.repository.OrigImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Controller
@RequiredArgsConstructor
public class ResultController {
    private final AnalysisResultRepository analysisResultRepository;
    private final OrigImageRepository origImageRepository;

    @GetMapping("/result")
    public String getResult(Model model) {
//        OrigImage origImage = origImageRepository.findById(id).orElse(null);
//        String oriBase64 = Base64.getEncoder().encodeToString(origImage.getImageData());
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
//        String analysisDate = origImage.getAnalysisDate().format(formatter);
//
//        AnalysisResult result = analysisResultRepository.findByOrigImgId(origImage.getId());
//
//        AnalysisImage rcnnImg = analysisImgRepository.findAllByOrigImgIdAndType(origImage.getId(),10).orElse(null);
//        String rcnnBase64 = Base64.getEncoder().encodeToString(rcnnImg.getImageData());
//
//        AnalysisImage opencvProImg = analysisImgRepository.findAllByOrigImgIdAndType(origImage.getId(),20).orElse(null);
//        String opencvProBase64 = Base64.getEncoder().encodeToString(opencvProImg.getImageData());
//
//        AnalysisImage opencvResultImg = analysisImgRepository.findAllByOrigImgIdAndType(origImage.getId(),25).orElse(null);
//        String opencvResultBase64 = Base64.getEncoder().encodeToString(opencvResultImg.getImageData());
//
//        AnalysisImage pcaImg = analysisImgRepository.findAllByOrigImgIdAndType(origImage.getId(),30).orElse(null);
//        String pcaBase64 = Base64.getEncoder().encodeToString(pcaImg.getImageData());
//
//        double avgPlastic = analysisResultRepository.getPlasticAvg();
//        double avgVinyl = analysisResultRepository.getVinylAvg();
//        double avgWood = analysisResultRepository.getWoodAvg();
//
//        double total = result.getWood()+ result.getPlastic() + result.getVinyl();
//        String suitable = (result.isSuitable()) ? "적합" : "부적합";
//
//        ResultDTO resultDTO = new ResultDTO(analysisDate,
//                oriBase64,
//                rcnnBase64,
//                opencvProBase64,
//                opencvResultBase64,
//                pcaBase64,
//                result.getPlastic(),
//                result.getVinyl(),
//                result.getWood(),
//                avgPlastic,
//                avgVinyl,
//                avgWood,
//                total,
//                suitable);
//
//        model.addAttribute("result", resultDTO);

        return "result";
    }

}
