package com.example.resource.restcontroller;

import com.example.resource.entity.Member;
import com.example.resource.repository.MemberRepository;
import com.example.resource.service.AnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class AnalysisApiController {

    private final AnalysisService analysisService;
    private final MemberRepository memberRepository;

    @PostMapping("/upload")
    public Mono<ResponseEntity<Map<String, Object>>> uploadImage(@RequestParam("image") MultipartFile image,
                                                                 Principal principal) {
        return Mono.fromCallable(() ->
                        memberRepository.findByUsername(principal.getName())
                                .orElseThrow(() -> new RuntimeException("사용자 정보 없음")))
                .flatMap(member ->
                        analysisService.saveAnalysisResultAndGetOrigImgId(image, member)
                                .map(id -> ResponseEntity.ok(Map.<String, Object>of("id", id)))
                )
                .onErrorResume(e -> {
                    ResponseEntity<Map<String, Object>> resp = ResponseEntity.status(500)
                            .body(Map.of("error", e.getMessage()));
                    return Mono.just(resp);
                });
    }

}



//package com.example.resource.restcontroller;
//
//
//import com.example.resource.entity.Member;
//import com.example.resource.repository.MemberRepository;
//import com.example.resource.service.AnalysisService;
//import jakarta.servlet.http.HttpSession;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.security.Principal;
//import java.util.Map;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/images")
//public class AnalysisApiController {
//    private final AnalysisService analysisService;
//    private final MemberRepository memberRepository;
//
//    @PostMapping("upload")
//    public ResponseEntity<Map<String, Object>> uploadImage(@RequestParam("image") MultipartFile image, Principal principal) throws IOException {
//        try{
//            Member member = memberRepository.findByUsername(principal.getName()).orElseThrow();
//            Long id = analysisService.saveAnalysisResultAndGetOrigImgId(image, member);
//
//
//            return ResponseEntity.ok(Map.of("id", id));
//        }catch(Exception e){
//            return ResponseEntity.status(500).body(Map.of("error", "파일 저장 실패"));
//        }
//
//    }
//}
//
//
