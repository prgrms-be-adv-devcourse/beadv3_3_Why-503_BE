package io.why503.aiservice.domain.ai.model.embedding;

//공연의 종류마다 이름을 찾거나 category 책임 분리하여 복잡한 코드 -> 단순화 코드 (공연의 종류 찾을 때)
public interface ShowGenre {


    //공연 카테고리
    ShowCategory getCategory();
    //공연 장르
    String getName();


    //문자열 반환
    static ShowGenre fromString(String value) {
        for (ClassicType type : ClassicType.values()) {
            if (type.getName().equalsIgnoreCase(value)) return type;
        }
        for (MusicalType type : MusicalType.values()) {
            if (type.getName().equalsIgnoreCase(value)) return type;
        }
        // 다른 타입들도 추가
        throw new IllegalArgumentException("잘못된 장르: " + value);
    }

    //카테고리와 장르 연결
    default boolean matches(String value) {
        return getName().equalsIgnoreCase(value);
    }




}
