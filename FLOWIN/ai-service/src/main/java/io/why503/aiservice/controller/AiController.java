package io.why503.aiservice.controller;


import io.why503.aiservice.model.vo.*;
import io.why503.aiservice.service.AiService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/recommend")
public class AiController {
    private final AiService aiService;
    private final VectorStore vectorStore;
    //추천 결과값 반환
    @PostMapping
    public ResultResponse getRecommendations(
            @RequestBody ResultRequest r
    ) {
        return aiService.getRecommendations(r);
    }


    @PostConstruct
    public void init() {
        log.info("장르 데이터 초기화 시작!");
        generateCategoryList().forEach(c -> upsert(c));
        log.info("장르 데이터 초기화 끝!");
    }

    private void upsert(Category c) {

        String content = switch (c) {
            case MUSICAL -> """
            장르: MUSICAL
            설명: 음악과 스토리가 결합된 공연
            특징: 감정선, 넘버, 무대 연출
            추천 대상: 서사와 음악을 동시에 즐기는 관객
            """;
            case CONCERT -> """
            장르: CONCERT
            설명: 라이브 음악 중심 공연
            특징: 현장감, 아티스트 중심
            추천 대상: 음악 몰입을 원하는 관객
            """;
            case PLAY -> """
            장르: PLAY
            설명: 대사와 연기 중심의 연극
            특징: 메시지, 배우 연기력
            추천 대상: 스토리 해석을 즐기는 관객
            """;
            case CLASSIC -> """
            장르: CLASSIC
            설명: 클래식 음악 기반 공연
            특징: 오케스트라, 정제된 분위기
            추천 대상: 차분한 감상을 원하는 관객
            """;
        };

        HashMap<String, Object> meta = new HashMap<>();
        meta.put("category", c.name());
        meta.put("mood", c.getMood());

        Document doc = Document.builder()
                .id("category_" + c.name())
                .text(content)
                .metadata(meta)
                .build();

        vectorStore.add(List.of(doc));
    }

    private List<Category> generateCategoryList() {
        return List.of(
                Category.MUSICAL,
                Category.CONCERT,
                Category.PLAY,
                Category.CLASSIC
        );
    }

}