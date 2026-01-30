package io.why503.aiservice.model.vo;

public enum Reason {
    // Concert
    CONCERT_LIVE_PREFERENCE("라이브 취향에 딱"),
    CONCERT_ENERGY_STYLE("에너지형 공연 추천"),

    // Musical
    MUSICAL_STORY_MUSIC("스토리+음악 취향"),
    MUSICAL_EMOTIONAL_FLOW("감성 몰입도 높음"),

    // Play
    PLAY_DIALOG_FOCUS("대사 중심 취향"),
    PLAY_CONCENTRATION("집중형 공연 추천"),

    // Classic
    CLASSIC_CALM_STYLE("차분한 취향 분석"),
    CLASSIC_FOCUS_LISTENING("집중 감상 선호"),

    // Fallback
    AI_BASED("AI 맞춤 추천"),
    GENRE_EXPANSION("장르 확장 추천"),
    POPULARITY_BASED("인기 기반 추천");

    private final String message;

    Reason(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}
