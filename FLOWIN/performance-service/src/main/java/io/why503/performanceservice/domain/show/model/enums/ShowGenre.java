package io.why503.performanceservice.domain.show.model.enums;

import io.why503.performanceservice.domain.show.util.ShowExceptionFactory;

public enum ShowGenre {
    BALLAD,        //발라드
    ROCK,          //락
    METAL,         //메탈
    HIPHOP,        //힙합
    R_N_B,           //R&B
    JAZZ,          //JAZZ
    TROT,          //트로트
    DRAMA,         //정극
    COMEDY,        //코미디
    ROMANCE,       //로맨스
    THRILLER,      //스릴러
    MYSTERY,       //미스테리
    HISTORICAL,    //시대극
    MONODRAMA,     //1인극
    CREATIVE,      //창작뮤지컬
    LICENSED,      //라이센스뮤지컬
    ORIGINAL,      //오리지널 내한
    ORCHESTRA,     //교향곡, 대규모 오케스트라
    CONCERTO,      //협주곡, 독주자와 오케스트라
    CHAMBER,       //실내악, 소규모 앙상블
    RECITAL,       //독주
    OPERA,         //오페라
    VOCAL,         //성악
    CHOIR;         //합창

    public static ShowGenre fromCode(String code) {
        try {
            return ShowGenre.valueOf(code);
        } catch (Exception e) {
            throw ShowExceptionFactory.showBadRequest(
                    "유효하지 않은 ShowGenre 값: " + code
            );
        }
    }
}
