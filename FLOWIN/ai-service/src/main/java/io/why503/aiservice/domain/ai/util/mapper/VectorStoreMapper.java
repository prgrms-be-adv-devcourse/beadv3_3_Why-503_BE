package io.why503.aiservice.domain.ai.util.mapper;

import io.why503.aiservice.global.client.performance.model.dto.response.PerformanceResponse;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 *  * -> VectorStore에서 쓰이는 형태로 변환
 */
@Component
public class VectorStoreMapper {

    public Document performanceToDocument(PerformanceResponse response) {
        String content = """
                카테고리: %s,
                장르: %s,
                공연명: %s
                """.formatted(
                        response.category(),
                        response.genre(),
                        response.showName()
        );
        return Document.builder()
                .id(response.showSq().toString())
                .text(content)
                .metadata(Map.of(
                        "type", "PERFORMANCE",
                        "sq", response.showSq(),
                        "name", response.showName(),
                        "showCategory", response.category(),
                        "genre", response.genre()
                ))
                .build();
    }
    //검색을 위한 string 생성
    public String SearchToQuery(String category, String genre){
        return """
                카테고리: %s,
                장르: %s,
                """.formatted(
                        category,
                        genre
        );
    }
}
