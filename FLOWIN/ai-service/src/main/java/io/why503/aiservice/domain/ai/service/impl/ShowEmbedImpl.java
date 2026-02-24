package io.why503.aiservice.domain.ai.service.impl;

import io.why503.aiservice.domain.ai.model.embedding.Performance;
import io.why503.aiservice.domain.ai.model.embedding.ShowCategory;
import io.why503.aiservice.domain.ai.model.embedding.genre.ShowGenre;
import io.why503.aiservice.domain.ai.service.ShowEmbed;
import io.why503.aiservice.global.client.PerformanceClient;
import io.why503.aiservice.global.client.dto.response.PerformanceResponse;
import io.why503.aiservice.global.client.entity.mapper.PerformanceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShowEmbedImpl implements ShowEmbed {

    private final VectorStore vectorStore;
    private final PerformanceClient performanceClient;
    private final PerformanceMapper performanceMapper;
    private final Performance performance;


    public void upsert(List<PerformanceResponse> performanceResponses) {
        List<Performance> performances = performanceResponses.stream()
                .map(response -> performanceMapper.PerformanceResponseToPerformance(response))
                .toList();

        if (performances.isEmpty()) {
            log.warn("임베딩할 공연이 없습니다.");
            return;
        }


        log.info("공연 {}건 메모리 저장 완료", performances.size());

        List<Document> documents = performances.stream()
                .map(p -> performance.toDocument(p))
                .toList();

        vectorStore.add(documents);
        log.info("공연 {}건 vectorStore upsert 완료", documents.size());

    }

    public void syncShows(ShowCategory category, ShowGenre genre) {
        List<PerformanceResponse> responses =
                performanceClient.getShowCategoryGenre(category, genre);

        upsert(responses);
    }
}



