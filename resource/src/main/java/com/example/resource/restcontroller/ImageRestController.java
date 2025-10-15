
package com.example.resource.restcontroller;


import com.example.resource.entity.OrigImage;
import com.example.resource.repository.OrigImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ImageRestController {
    private final OrigImageRepository origImageRepository;

    @PostMapping("/api/images/upload")
    public ResponseEntity<Map<String, Object>> uploadImage(@RequestParam("image") MultipartFile file){
        try{

            OrigImage origImage = new OrigImage();
            origImage.setImageData(file.getBytes());
            origImage.setAnalysisDate(LocalDateTime.now());

            OrigImage saved = origImageRepository.save(origImage);

            Map<String, Object> response = new HashMap<>();
            response.put("id",saved.getId());
            return ResponseEntity.ok(response);
        } catch(IOException e){
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error","파일 저장 실패"));
        }
    }
}
