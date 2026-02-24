package io.why503.aiservice.domain.ai.model.embedding.genre;

import io.why503.aiservice.domain.ai.model.embedding.ShowCategory;

//클래식
public enum ClassicType implements ShowGenre {
    ORCHESTRA(ShowCategory.CLASSIC,"교향곡"),     //교향곡, 대규모 오케스트라
    CONCERTO(ShowCategory.CLASSIC,"협주곡"),      //협주곡, 독주자와 오케스트라
    CHAMBER(ShowCategory.CLASSIC,"실내악"),       //실내악, 소규모 앙상블
    RECITAL(ShowCategory.CLASSIC,"독주"),       //독주
    OPERA(ShowCategory.CLASSIC,"오페라"),         //오페라
    VOCAL(ShowCategory.CLASSIC,"성악"),         //성악
    CHOIR(ShowCategory.CLASSIC,"합창");         //합창

    private final ShowCategory showCategory;
    private final String Name;


    //장르 이름
    ClassicType(ShowCategory showCategory, String Name) {
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
        for (ClassicType type : values()) {
            if (type.name().equalsIgnoreCase(genre) || type.getName().equalsIgnoreCase(genre)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown genre: " + genre);
    }

}
