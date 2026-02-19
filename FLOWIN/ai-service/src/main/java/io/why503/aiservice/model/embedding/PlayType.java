package io.why503.aiservice.model.embedding;

import lombok.Getter;

import java.util.Arrays;
import java.util.Set;

//연극
public enum PlayType implements ShowCategory {
    TROT(Category.PLAY,"트로트", Set.of(MoodCategory.ROMANCE)),          //트로트
    DRAMA(Category.PLAY,"정극", Set.of(MoodCategory.ROMANCE)),         //정극
    COMEDY(Category.PLAY,"코미디", Set.of(MoodCategory.COMEDY)),        //코미디
    ROMANCE(Category.PLAY,"로맨스", Set.of(MoodCategory.ROMANCE)),       //로맨스
    THRILLER(Category.PLAY,"스릴러", Set.of(MoodCategory.HORROR)),      //스릴러
    MYSTERY(Category.PLAY,"미스테리", Set.of(MoodCategory.HORROR)),       //미스테리
    HISTORICAL(Category.PLAY,"시대극", Set.of(MoodCategory.FANTASY)),    //시대극
    MONODRAMA(Category.PLAY,"1인극", Set.of(MoodCategory.ROMANCE));     //1인극

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
