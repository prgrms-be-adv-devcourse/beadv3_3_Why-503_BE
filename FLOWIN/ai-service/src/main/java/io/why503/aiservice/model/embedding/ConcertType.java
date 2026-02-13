package io.why503.aiservice.model.embedding;

import lombok.Getter;

import java.util.Arrays;
import java.util.Set;

//콘서트
public enum ConcertType implements ShowCategory {
    BALLAD(Category.CONCERT,"발라드", Set.of(MoodCategory.ROMANCE)), //발라드
    HIPHOP(Category.CONCERT,"힙합", Set.of(MoodCategory.ACTION)), //힙합
    JAZZ(Category.CONCERT,"재즈", Set.of(MoodCategory.ACTION)), //JAZZ
    METAL(Category.CONCERT,"메탈", Set.of(MoodCategory.ACTION)), //메탈
    ROCK(Category.CONCERT,"락", Set.of(MoodCategory.ROMANCE)), //락
    R_N_B(Category.CONCERT,"리듬앤블루스", Set.of(MoodCategory.FANTASY)), //R&B
    TROT(Category.CONCERT,"트로트", Set.of(MoodCategory.COMEDY)); //트로트


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
