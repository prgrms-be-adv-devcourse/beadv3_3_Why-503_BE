package io.why503.aiservice.model.vo;

import lombok.Getter;
import java.util.*;

//장르 속성 추가
public enum Category {
    //콘서트
    CONCERT("현장감, 몰입", Set.of(ConcertType.values())),
    //뮤지컬
    MUSICAL("감동, 서사, 스토리 중심", Set.of(MusicalType.values())),
    //연극
    PLAY("대화 중심", Set.of(PlayType.values())),
    //클래식
    CLASSIC("차분, 정제된 분위기", Set.of(ClassicType.values()));
    //Category.CONCERT.supports(ConcertType.ROCK);

    @Getter
    private final String mood;
    @Getter
    private final Set<? extends ShowCategory> types;


    //카테고리 생성자
    Category(String mood, Set<? extends ShowCategory> types) {
        this.mood = mood;
        this.types = types;
    }


    //속성 json 인식할 때 문자열만 인식으로 인한 장르 문자열 바꿔줌
    public static Optional<Category> fromString(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        // 1. 원본 그대로
        Optional<Category> direct = tryParse(value);
        if (direct.isPresent()) return direct;

        // 2. 공백 제거 + 대소문자
        String normalized = value.trim().toUpperCase();
        Optional<Category> normalizedResult = tryParse(normalized);
        if (normalizedResult.isPresent()) return normalizedResult;

        // 3. 특수문자 제거
        String filtered = normalized.replaceAll("[^A-Z]", "");
        Optional<Category> filteredResult = tryParse(filtered);
        if (filteredResult.isPresent()) return filteredResult;

        // 4. 실패시 null
        return Optional.empty();
    }

    private static Optional<Category> tryParse(String value) {
        try {
            return Optional.of(Category.valueOf(value));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }



    public ShowCategory findShowType(String raw) {

        if (raw == null || raw.isBlank()) {
            return types.stream().findFirst().orElse(null);
        }

        String normalized = raw.trim().toLowerCase();

        return types.stream()
                .filter(type ->
                        type.typeName().equalsIgnoreCase(normalized)
                                || ((Enum<?>) type).name().equalsIgnoreCase(normalized)
                )
                .findFirst()
                .orElse(null);
    }


    //콘서트
    public enum ConcertType implements ShowCategory{
        BALLAD(Category.CONCERT,"발라드", Set.of(MoodCategory.FANTASY)), //발라드
        HIPHOP(Category.CONCERT,"힙합", Set.of(MoodCategory.ACTION)), //힙합
        JAZZ(Category.CONCERT,"재즈", Set.of(MoodCategory.ROMANCE)), //JAZZ
        METAL(Category.CONCERT,"메탈", Set.of(MoodCategory.ACTION)), //메탈
        ROCK(Category.CONCERT,"락", Set.of(MoodCategory.FANTASY)), //락
        R_N_B(Category.CONCERT,"리듬앤블루스", Set.of(MoodCategory.FANTASY)), //R&B
        TROT(Category.CONCERT,"트로트", Set.of(MoodCategory.FANTASY)); //트로트

        @Getter
        private final Category category;
        @Getter
        private final String typeName;
        @Getter
        private final Set<MoodCategory> mood;

        ConcertType(Category category,String typeName, Set<MoodCategory> mood) {
            this.category = category;
            this.typeName = typeName;
            this.mood = mood;
        }

        @Override
        public Category getCategory() {
            return category;
        }

        @Override
        public String typeName() {
            return typeName;
        }
        @Override
        public Set<MoodCategory> moods() {
            return mood;
        }


    }


    //연극
    enum PlayType implements ShowCategory{
        TROT(Category.PLAY,"트로트", Set.of(MoodCategory.FANTASY)),          //트로트
        DRAMA(Category.PLAY,"정극", Set.of(MoodCategory.FANTASY)),         //정극
        COMEDY(Category.PLAY,"코미디", Set.of(MoodCategory.COMEDY)),        //코미디
        ROMANCE(Category.PLAY,"로맨스", Set.of(MoodCategory.ROMANCE)),       //로맨스
        THRILLER(Category.PLAY,"스릴러", Set.of(MoodCategory.ACTION)),      //스릴러
        MYSTERY(Category.PLAY,"미스테리", Set.of(MoodCategory.FANTASY)),       //미스테리
        HISTORICAL(Category.PLAY,"시대극", Set.of(MoodCategory.HORROR)),    //시대극
        MONODRAMA(Category.PLAY,"1인극", Set.of(MoodCategory.HORROR));     //1인극
        @Getter
        private final Category category;
        @Getter
        private final String typeName;
        @Getter
        private final Set<MoodCategory> mood;

        PlayType(Category category,String typeName, Set<MoodCategory> mood) {
            this.category = category;
            this.typeName = typeName;
            this.mood = mood;
        }


        @Override
        public Category getCategory() {
            return category;
        }

        @Override
        public String typeName() {
            return typeName;
        }
        @Override
        public Set<MoodCategory> moods() {
            return mood;
        }
    }

    //뮤지컬
    public enum MusicalType implements ShowCategory{
        CREATIVE(Category.MUSICAL,"창작", Set.of(MoodCategory.FANTASY)),      //창작뮤지컬
        LICENSED(Category.MUSICAL,"라이센스", Set.of(MoodCategory.FANTASY)),      //라이센스뮤지컬
        ORIGINAL(Category.MUSICAL,"오리지널", Set.of(MoodCategory.FANTASY));    //오리지널 내한

        @Getter
        private final Category category;
        @Getter
        private final String typeName;
        @Getter
        private final Set<MoodCategory> mood;

        MusicalType(Category category,String typeName, Set<MoodCategory> mood) {
            this.category = category;
            this.typeName = typeName;
            this.mood = mood;
        }
        @Override
        public Category getCategory() {
            return category;
        }

        @Override
        public String typeName() {
            return typeName;
        }
        @Override
        public Set<MoodCategory> moods() {
            return mood;
        }
    }

    //클래식
    enum ClassicType implements ShowCategory{
        ORCHESTRA(Category.CLASSIC,"교향곡", Set.of(MoodCategory.FANTASY)),     //교향곡, 대규모 오케스트라
        CONCERTO(Category.CLASSIC,"협주곡", Set.of(MoodCategory.FANTASY)),      //협주곡, 독주자와 오케스트라
        CHAMBER(Category.CLASSIC,"실내악", Set.of(MoodCategory.FANTASY)),       //실내악, 소규모 앙상블
        RECITAL(Category.CLASSIC,"독주", Set.of(MoodCategory.FANTASY)),       //독주
        OPERA(Category.CLASSIC,"오페라", Set.of(MoodCategory.FANTASY)),         //오페라
        VOCAL(Category.CLASSIC,"성악", Set.of(MoodCategory.FANTASY)),         //성악
        CHOIR(Category.CLASSIC,"합창", Set.of(MoodCategory.FANTASY));         //합창

        @Getter
        private final Category category;
        @Getter
        private final String typeName;
        @Getter
        private final Set<MoodCategory> mood;

        ClassicType(Category category,String typeName, Set<MoodCategory> mood) {
            this.category = category;
            this.typeName = typeName;
            this.mood = mood;
        }

        @Override
        public Category getCategory() {
            return category;
        }

        @Override
        public String typeName() {
            return typeName;
        }
        @Override
        public Set<MoodCategory> moods() {
            return mood;
        }
    }


}
