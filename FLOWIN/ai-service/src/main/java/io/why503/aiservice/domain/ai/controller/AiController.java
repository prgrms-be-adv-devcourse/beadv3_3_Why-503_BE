package io.why503.aiservice.domain.ai.controller;

import io.why503.aiservice.domain.ai.model.embedding.ShowCategory;
import io.why503.aiservice.domain.ai.model.embedding.genre.impl.ShowGenre;
import io.why503.aiservice.domain.ai.model.vo.ResultRequest;
import io.why503.aiservice.domain.ai.model.vo.ResultResponse;
import io.why503.aiservice.domain.ai.service.AiService;
import io.why503.aiservice.domain.ai.service.ShowEmbedService;
import io.why503.aiservice.global.client.PerformanceClient;
import io.why503.aiservice.global.client.dto.response.PerformanceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/recommend")
public class AiController {

    private final AiService aiService;
    private final VectorStore vectorStore;
    private final ShowEmbedService showEmbedService;
    private final PerformanceClient performanceClient;
    private final CategoryDocument categoryDocument;

//    @PostConstruct
    @EventListener(ApplicationReadyEvent.class)
    public void init(ShowCategory category, ShowGenre genre) {
        log.info("장르 데이터 초기화 시작!");
        //카테고리 문서
        upsert();
        //공연 문서
        List<PerformanceResponse> responses =
                performanceClient.getShowCategoryGenre(category, genre);
        showEmbedService.upsert(responses);
        log.info("장르 데이터 초기화 끝!");
    }


    //카테고리 문서 생성
    public void upsert() {
        for (ShowCategory c : ShowCategory.values()) {
            vectorStore.add(List.of(categoryDocument.create(c)));
        }
    }

    //추천 결과값 반환
    @PostMapping
    public CompletableFuture<ResultResponse> getRecommendations(
            @RequestBody ResultRequest request, Long userSq, ShowCategory category, ShowGenre genre
    ) {
        return aiService.getRecommendations( request, userSq, category, genre);
    }


    //공연 동기화
    @GetMapping("/performances")
    public void show(ShowCategory category, ShowGenre genre) {
        showEmbedService.syncShows(category, genre);
    }
}