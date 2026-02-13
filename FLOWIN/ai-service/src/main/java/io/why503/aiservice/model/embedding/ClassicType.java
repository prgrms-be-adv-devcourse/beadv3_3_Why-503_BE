package io.why503.aiservice.model.embedding;

import lombok.Getter;

import java.util.Arrays;
import java.util.Set;

//클래식
public enum ClassicType implements ShowCategory {
    ORCHESTRA(Category.CLASSIC,"교향곡", Set.of(MoodCategory.FANTASY)),     //교향곡, 대규모 오케스트라
    CONCERTO(Category.CLASSIC,"협주곡", Set.of(MoodCategory.FANTASY)),      //협주곡, 독주자와 오케스트라
    CHAMBER(Category.CLASSIC,"실내악", Set.of(MoodCategory.ROMANCE)),       //실내악, 소규모 앙상블
    RECITAL(Category.CLASSIC,"독주", Set.of(MoodCategory.ROMANCE)),       //독주
    OPERA(Category.CLASSIC,"오페라", Set.of(MoodCategory.FANTASY)),         //오페라
    VOCAL(Category.CLASSIC,"성악", Set.of(MoodCategory.ROMANCE)),         //성악
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
