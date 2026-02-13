package io.why503.aiservice.model.embedding;

import java.util.*;

//공연의 종류마다 이름을 찾거나 category 책임 분리하여 복잡한 코드 -> 단순화 코드 (공연의 종류 찾을 때)
public interface ShowCategory {


    //아직 없음
    Category getCategory();
    //공연 이름
    String typeName();
    //분위기
    Set<MoodCategory> moods();


    //선정된 분위기 설정
    default Set<MoodCategory> pickMood(ShowCategory showCategory, Map<MoodCategory, Double> moodScores) {
        if (showCategory.moods() == null || showCategory.moods().isEmpty()) {
            return Set.of();
        }
        return showCategory.moods().stream()
                .filter(moodCategory -> moodScores.getOrDefault(moodCategory, 0.0) > 0)
                .max(Comparator.comparing(moodCategory -> moodScores.get(moodCategory)))
                .map(moodCategory -> Set.of(moodCategory)) // 단일 값도 Set으로 변환
                .orElse(Set.of());
    }
}
