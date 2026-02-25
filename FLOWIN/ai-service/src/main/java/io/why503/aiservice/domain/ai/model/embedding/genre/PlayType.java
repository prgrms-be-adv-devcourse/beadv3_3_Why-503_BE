package io.why503.aiservice.domain.ai.model.embedding.genre;

import io.why503.aiservice.domain.ai.model.embedding.ShowCategory;

//연극
public enum PlayType implements ShowGenre {
    TROT(ShowCategory.PLAY,"트로트"),          //트로트
    DRAMA(ShowCategory.PLAY,"정극"),         //정극
    COMEDY(ShowCategory.PLAY,"코미디"),        //코미디
    ROMANCE(ShowCategory.PLAY,"로맨스"),       //로맨스
    THRILLER(ShowCategory.PLAY,"스릴러"),      //스릴러
    MYSTERY(ShowCategory.PLAY,"미스테리"),       //미스테리
    HISTORICAL(ShowCategory.PLAY,"시대극"),    //시대극
    MONODRAMA(ShowCategory.PLAY,"1인극");     //1인극

    private final ShowCategory showCategory;
    private final String Name;

    //공연 이름
    PlayType(ShowCategory showCategory, String Name) {
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
        for (PlayType type : values()) {
            if (type.name().equalsIgnoreCase(genre) || type.getName().equalsIgnoreCase(genre)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown genre: " + genre);
    }

}
