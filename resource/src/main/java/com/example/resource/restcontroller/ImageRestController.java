
package com.example.resource.restcontroller;


import com.example.resource.security.MemberDetails;
import com.example.resource.service.ImageAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ImageRestController {
    private final ImageAnalysisService imageAnalysisService;

    @PostMapping("/api/images/upload")
    public Mono<ResponseEntity<Map<String, Object>>> uploadImage(@RequestParam("image") MultipartFile file,
                                                                 @AuthenticationPrincipal MemberDetails memberDetails) {
        return imageAnalysisService.analyzeImage(file, memberDetails.getMember())
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("status", 1))));
    }
}
