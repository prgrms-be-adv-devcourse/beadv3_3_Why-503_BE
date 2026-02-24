package io.why503.aiservice.domain.ai.model.embedding.genre;

import io.why503.aiservice.domain.ai.model.embedding.ShowCategory;

//콘서트
public enum ConcertType implements ShowGenre {
    BALLAD(ShowCategory.CONCERT,"발라드"), //발라드
    HIPHOP(ShowCategory.CONCERT,"힙합"), //힙합
    JAZZ(ShowCategory.CONCERT,"재즈"), //JAZZ
    METAL(ShowCategory.CONCERT,"메탈"), //메탈
    ROCK(ShowCategory.CONCERT,"락"), //락
    R_N_B(ShowCategory.CONCERT,"리듬앤블루스"), //R&B
    TROT(ShowCategory.CONCERT,"트로트"); //트로트


    private final ShowCategory showCategory;
    private final String Name;

    //장르 이름
    ConcertType(ShowCategory showCategory, String Name) {
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

    public static ShowGenre fromString(String genre) {
        for (ConcertType type : values()) {
            if (type.name().equalsIgnoreCase(genre) || type.getName().equalsIgnoreCase(genre)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown genre: " + genre);
    }
}
