package io.why503.aiservice.global.client.entity.mapper;

import io.why503.aiservice.global.client.dto.response.Performance;
import io.why503.aiservice.global.client.dto.response.PerformanceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PerformanceMapper {

    public Performance responseToPerformance(PerformanceResponse response) {
        return new Performance(
                response.showSq(),
                response.showName(),
                response.category(),
                response.genre()
        );
    }
    //공연 문서 반환
    public Performance docToPerformance(Document doc) {
        try {
            return new Performance(
                    Long.valueOf(doc.getMetadata().get("sq").toString()),
                    doc.getMetadata().get("name").toString(),
                    doc.getMetadata().get("category").toString(),
                    doc.getMetadata().get("genre").toString()
            );
        } catch (Exception e) {
            log.error("Performance 변환 실패: doc={}", doc, e);
            throw new IllegalStateException("Performance 변환 실패", e);
        }
    }
}