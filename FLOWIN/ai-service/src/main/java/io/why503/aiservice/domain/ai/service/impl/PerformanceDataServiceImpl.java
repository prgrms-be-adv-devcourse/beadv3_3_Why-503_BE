package io.why503.aiservice.domain.ai.service.impl;

import io.why503.aiservice.domain.ai.service.PerformanceDataService;
import io.why503.aiservice.domain.ai.util.AiExceptionFactory;
import io.why503.aiservice.domain.ai.util.mapper.VectorStoreMapper;
import io.why503.aiservice.global.client.performance.PerformanceClient;
import io.why503.aiservice.global.client.performance.model.dto.response.PerformanceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PerformanceDataServiceImpl implements PerformanceDataService {
    //벡터 db
    private final VectorStore vectorStore;
    private final PerformanceClient performanceClient;
    private final VectorStoreMapper vectorStoreMapper;

    @Override
    public void update() {
        log.info("장르 데이터 업데이트 시작");
        performanceDataUpsert();
        log.info("장르 데이터 업데이트 완료");
    }

    @EventListener(ApplicationReadyEvent.class)
    private void init() {
        log.info("장르 데이터 초기화 시작");
        performanceDataUpsert();
        log.info("장르 데이터 초기화 완료");
    }

    private void performanceDataUpsert(){
        //공연 불러오기
        List<PerformanceResponse> responses = performanceClient.getShowAll();

        if (responses.isEmpty()) {
            throw AiExceptionFactory.AiNoContent("임베딩할 공연이 없음");
        }
        log.info("공연 {}건 불러오기 완료", responses.size());
        List<Document> documentList = responses.stream()
                .map(i -> vectorStoreMapper.performanceToDocument(i))
                .toList();
        vectorStore.add(documentList);

        log.info("공연 {}건 vectorStore upsert 완료", documentList.size());
    }
}
