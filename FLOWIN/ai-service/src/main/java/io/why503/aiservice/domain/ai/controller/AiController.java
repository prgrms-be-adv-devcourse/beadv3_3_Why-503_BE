package io.why503.aiservice.domain.ai.controller;

import io.why503.aiservice.domain.ai.model.embedding.ShowCategory;
import io.why503.aiservice.domain.ai.model.embedding.genre.ShowGenre;
import io.why503.aiservice.domain.ai.model.embedding.genre.ShowGenreResolver;
import io.why503.aiservice.domain.ai.model.vo.ResultRequest;
import io.why503.aiservice.domain.ai.model.vo.ResultResponse;
import io.why503.aiservice.domain.ai.service.impl.AiServiceImpl;
import io.why503.aiservice.domain.ai.service.impl.ShowEmbedImpl;
import io.why503.aiservice.global.client.PerformanceClient;
import io.why503.aiservice.global.client.dto.response.PerformanceResponse;
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

    private final AiServiceImpl aiServiceImpl;
    private final VectorStore vectorStore;
    private final ShowEmbedImpl showEmbedImpl;
    private final PerformanceClient performanceClient;
    private final CategoryDocument categoryDocument;

//    @PostConstruct
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        log.info("장르 데이터 초기화 시작!");
        
        //카테고리 문서
        upsert();

        //공연 문서
        List<PerformanceResponse> responses =performanceClient.getShowAll();
        showEmbedImpl.upsert(responses);
        log.info("장르 데이터 초기화 끝!");
    }


    //카테고리 문서 생성
    private void upsert() {
        for (ShowCategory c : ShowCategory.values()) {
            vectorStore.add(List.of(categoryDocument.create(c)));
        }
    }

    //추천 결과값 반환
    @PostMapping
    public ResultResponse getRecommendations(
            @RequestBody ResultRequest request,
            @RequestHeader("X-USER-SQ") Long userSq
    ) {
        ShowCategory category = ShowCategory.fromString(request.showCategory());
        ShowGenre genre = category.findShowType(request.genre());

        return aiServiceImpl.getRecommendations(userSq, category, genre);
    }


    //공연 동기화
    @GetMapping("/performances")
    public void show(ShowCategory category, ShowGenre genre) {
        showEmbedImpl.syncShows(category, genre);
    }
}