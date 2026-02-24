package io.why503.aiservice.domain.ai.model.embedding.genre;

import io.why503.aiservice.domain.ai.model.embedding.ShowCategory;
import io.why503.aiservice.domain.ai.model.embedding.genre.impl.ShowGenre;
import lombok.Getter;

//뮤지컬
public enum MusicalType implements ShowGenre {
    CREATIVE(ShowCategory.MUSICAL,"창작"),      //창작뮤지컬
    LICENSED(ShowCategory.MUSICAL,"라이센스"),      //라이센스뮤지컬
    ORIGINAL(ShowCategory.MUSICAL,"오리지널");    //오리지널 내한

    @Getter
    private final ShowCategory showCategory;
    @Getter
    private final String Name;

    //장르 이름
    MusicalType(ShowCategory showCategory, String Name) {
        this.showCategory = showCategory;
        this.Name = Name;
    }
    @Override
    public ShowCategory getCategory() {
        return showCategory;
    }

    @Override
    public String getName() {
        return Name;
    }

}