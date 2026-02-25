package io.why503.aiservice.domain.ai.model.embedding;

import io.why503.aiservice.global.client.dto.response.Performance;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DocumentConverter {
    public Document toDocument(Performance p) {
        String content = """
        공연명: %s
        카테고리: %s
        장르: %s
        """.formatted(
                p.name(),
                p.category(),
                p.genre()
        );
        return Document.builder()
                .id(p.sq().toString())
                .text(content)
                .metadata(Map.of(
                        "sq", p.sq(),
                        "name", p.name(),
                        "category", p.category(),
                        "genre", p.genre()
                ))
                .build();
    }
}
