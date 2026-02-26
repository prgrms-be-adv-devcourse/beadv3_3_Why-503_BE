package io.why503.aiservice.domain.ai.controller;

import io.why503.aiservice.domain.ai.model.dto.response.RecommendResponse;
import io.why503.aiservice.domain.ai.service.AiService;
import io.why503.aiservice.domain.ai.service.PerformanceDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/recommend")
public class AiController {

    private final AiService aiService;
    private final PerformanceDataService performanceDataService;

    //추천 결과값 반환
    @GetMapping
    public ResponseEntity<List<RecommendResponse>> getRecommendations(
            @RequestHeader("X-USER-SQ") Long userSq
    ) {
        return ResponseEntity.ok(aiService.getRecommendations(userSq));
    }


    //공연 동기화
    @PostMapping("/update")
    public ResponseEntity<String> update() {
        performanceDataService.update();
        return ResponseEntity.ok("vector store 업데이트 완료");
    }
}