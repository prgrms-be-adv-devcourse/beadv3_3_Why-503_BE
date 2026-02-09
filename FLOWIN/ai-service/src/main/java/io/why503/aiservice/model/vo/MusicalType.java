package io.why503.aiservice.model.vo;

import lombok.Getter;

import java.util.Set;

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