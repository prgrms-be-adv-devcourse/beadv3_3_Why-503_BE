package io.why503.aiservice.domain.ai.model.embedding;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;

import java.util.Map;


@Slf4j
public record Performance(
        ShowCategory category,
        ShowGenre genre
) {



    //문서화 반환
    public static Document toDocument(Performance p) {


        String content = """
        카테고리: %s
        장르: %s
        """.formatted(
                p.category().name(),
                p.genre().getName()
        );

        return Document.builder()
                .text(content)
                .metadata(Map.of(
                        "type", "PERFORMANCE",
                        "category", p.category().name(),
                        "genre", p.genre().getName()
                ))
                .build();
    }

    //공연 문서 반환
    public static Performance toPerformance(Document doc) {
        try {
            ShowCategory category = ShowCategory.valueOf(doc.getMetadata().get("category").toString());
            ShowGenre showCategory = category.findShowType(doc.getMetadata().get("genre").toString());

            return new Performance(
                    category,
                    showCategory
            );
        } catch (Exception e) {
            log.error("Performance 변환 실패: doc={}", doc, e);
            return null;
        }
    }
}
