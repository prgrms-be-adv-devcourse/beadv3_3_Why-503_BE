package io.why503.aiservice.model.embedding;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVRecord;
import org.springframework.ai.document.Document;
import java.util.Map;
import java.util.Set;

@Slf4j
public record Performance(
        int sq,
        String name,
        @Getter
        Category category,
        ShowCategory genre,
        String startDate,
        String endDate,
        String hall,
        @Getter
        Set<MoodCategory> moods
) {


    public Performance(
            int sq,
            Category category,
            ShowCategory showCategory,
            String name,
            String hall,
            String startDate,
            String endDate,
            Set<MoodCategory> moods) {
        this(sq, name, category, showCategory, startDate, endDate, hall, moods);
    }

    public static Performance from(CSVRecord record, int index) {
        try {
            int sq = record.get(0).isBlank()
                    ? index
                    : Integer.parseInt(record.get(0));

            // 카테고리 변환
            Category finalCategory = Category.fromString(record.get("카테고리"))
                    .orElseThrow(() -> new IllegalArgumentException("잘못된 카테고리: " + record.get("카테고리")));

            // 장르 변환: 없는 장르면 null 반환
            ShowCategory showCategory;
            try {
                showCategory = finalCategory.findShowType(record.get("장르"));
            } catch (IllegalArgumentException e) {
                log.error("잘못된 장르: {} / 카테고리: {}", record.get("장르"), record.get("카테고리"));
                return null;
            }

            // Performance 객체 생성
            return new Performance(
                    sq,
                    record.get("공연명"),
                    finalCategory,
                    showCategory,
                    record.get("시작일"),
                    record.get("종료일"),
                    record.get("공연장"),
                    Set.of()
            );

        } catch (Exception e) {
            log.error("공연 CSV 파싱 실패, record={}", record.toMap(), e);
            return null;
        }
    }


    public static Document toDocument(Performance p) {

        String content = """
        공연 번호: %s
        공연 이름: %s
        카테고리: %s
        장르: %s
        공연장: %s
        시작일: %s
        종료일: %s
        """.formatted(
                p.sq(),
                p.name(),
                p.category().name(),
                p.genre().typeName(),
                p.hall(),
                p.startDate(),
                p.endDate()
        );

        return Document.builder()
                .id("performance_" + p.sq())
                .text(content)
                .metadata(Map.of(
                        "type", "PERFORMANCE",
                        "sq", p.sq(),
                        "category", p.category().name(),
                        "genre", p.genre().typeName(),
                        "name", p.name(),
                        "hall", p.hall(),
                        "startDate", p.startDate(),
                        "endDate", p.endDate(),
                        "moods", p.moods().stream()
                                .map(Enum::name)
                                .toList()
                ))
                .build();
    }
}
