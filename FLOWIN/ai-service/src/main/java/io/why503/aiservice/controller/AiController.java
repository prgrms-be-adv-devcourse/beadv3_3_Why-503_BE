package io.why503.aiservice.controller;

import io.why503.aiservice.model.embedding.*;
import io.why503.aiservice.model.vo.*;
import io.why503.aiservice.service.AiService;
import io.why503.aiservice.service.ShowEmbedService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/recommend")
public class AiController {

    private final AiService aiService;
    private final VectorStore vectorStore;
    private final ShowEmbedService showEmbedService;

//    @PostConstruct
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        log.info("장르 데이터 초기화 시작!");
//        upsert();
        showEmbedService.upsertCheck();
//        showEmbedService.watchFile();
        log.info("장르 데이터 초기화 끝!");
    }


    public void upsert() {
        for (Category c : Category.values()) {
            vectorStore.add(List.of(CategoryDocument.create(c)));
        }
    }

    //추천 결과값 반환
    @PostMapping
    public ResultResponse getRecommendations(
            @RequestBody ResultRequest r
    ) {
        return aiService.getRecommendations(r);
    }


    //학습된 공연 리스트 출력
    @GetMapping("/performances")
    public List<String> listPerformances() {
        return showEmbedService.getAllPerformancesJson();
    }
}