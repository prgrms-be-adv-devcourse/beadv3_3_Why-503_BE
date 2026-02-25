package io.why503.aiservice.domain.ai.model.embedding.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ShowGenreResolver {

    private final Map<String, ShowGenre> genreMap = new HashMap<>();

    public ShowGenreResolver(List<ShowGenre> genres) {
        for (ShowGenre g : genres) {
            // 1) 한글 이름 키
            putKey(g.getName(), g);

            // 2) enum 이름 키 (OPERA, BALLAD 등)
            putKey(g.toString(), g); // enum이면 toString() == name()

        }

        log.info("ShowGenreResolver loaded keys={}", genreMap.size());
    }

    private void putKey(String raw, ShowGenre genre) {
        if (raw == null) return;

        String key1 = normalize(raw);
        if (!key1.isBlank()) genreMap.putIfAbsent(key1, genre);

        // 특수문자 제거 버전 추가(예: "발라드!!!!" 같은 입력 방어)
        String key2 = normalize(raw.replaceAll("[^a-zA-Z가-힣]", ""));
        if (!key2.isBlank()) genreMap.putIfAbsent(key2, genre);
    }

    private String normalize(String s) {
        return s == null ? "" : s.trim().toUpperCase();
    }

    public ShowGenre fromString(String value) {
        ShowGenre g = fromStringOrNull(value);
        if (g == null) {
            throw new IllegalStateException("Unknown genre: " + value);
        }
        return g;
    }

    public ShowGenre fromStringOrNull(String value) {
        if (value == null || value.isBlank()) return null;
        return genreMap.get(normalize(value));
    }
}