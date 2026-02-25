package io.why503.aiservice.domain.ai.service.impl;

public class AiPromptImpl {
    private AiPromptImpl() {}
    public static final String prompt =  """
                        공연 장르 추천해주는 서비스입니다.
                        아래 사용자 정보와 공연 목록을 기반으로
                        [규칙]
                        1. 반드시 JSON만 출력한다.
                        2. showCategory 값은 다음 중 하나만 사용한다:
                           MUSICAL, CONCERT, PLAY, CLASSIC
                        3. mood 값은 다음중 하나만 사용한다:
                            FANTASY, HORROR, ROMANCE, COMEDY, ACTION
                        4. summary, explanations, recommendations 외 다른 텍스트 금지
                        JSON은 ``` 같은 코드블록으로 감싸지 말고
                        순수 JSON 텍스트만 출력하라.
                        5. recommendations는 사용자의 점수 기반 우선순위를 반영
                        - categoryScore: 각 카테고리 점수
                        - genreScore: 각 장르 점수
                        6. 절대로 문장이나 설명을 JSON 밖에 출력하지 마세요.

                        
                        
                        [Category 규칙]
                        - showCategory 값은 반드시 아래 중 하나만 사용:
                          MUSICAL, CONCERT, PLAY, CLASSIC
                        
                        [topFinalShow 규칙]
                        - topFinalShow 선택한 category에 속한 공연 종류 중 하나여야 한다.
                         MUSICAL: CREATIVE, LICENSED, ORIGINAL
                         CONCERT: BALLAD, ROCK, METAL, HIPHOP, R_N_B, JAZZ, TROT
                         PLAY: TROT, DRAMA, COMEDY, ROMANCE, THRILLER, MYSTERY, HISTORICAL, MONODRAMA
                         CLASSIC: ORCHESTRA, CONCERTO, CHAMBER, RECITAL, OPERA, VOCAL, CHOIR
                        - category와 무관한 topFinalShow 절대 선택하지 말 것.
                        
                        [ MoodCategory 규칙 ]
                        - mood 값은 반드시 아래 중 하나만 사용:
                          FANTASY, HORROR, ROMANCE, COMEDY, ACTION
                        - mood는 선택한 showCategory가 허용하는 mood 중 하나여야 한다.
                        
                        [ similarShow 규칙 ]
                        - topFinalShow 선택한 category에 속한 공연 종류 중 하나씩 최대 3~4개까지 선정한단.
                        - category와 무관한 topFinalShow 절대 선택하지 말 것.
                        
                        -----------------------------------------
                        [RAG 지식 문서]
                        아래 내용은 벡터 검색으로 찾은 문서입니다.
                        이 정보만을 근거로 추천을 생성하세요.
            
                        %s
                        -----------------------------------------
                        
                        [사용자 정보]
                        최근 구매한 카테고리: %s
                        구매한 장르:%s
                        
                        [상위 카테고리]
                        %s
                        
                        [상위 장르]
                        %s
                        
                        [카테고리 점수]
                        %s
                        
                        [장르 점수]
                        %s
                        
                        [추천 후보]
                        %s
                        
                        [유사 공연 리스트]
                        %s
                        
                        [출력 형식]
                        {
                           "summary": "...",
                           "userTopCategory": [],
                           "userTopGenre": [],
                           "categoryScore": {},
                           "genreScore": {},
                           "recommendations": [
                             {
                               "performanceName": "",
                               "showCategory": "",
                               "showCategory": "",
                               "score": 0.0,
                               "reason": "",
                               "topFinalShows": []
                               "similarShows": []
                             }
                           ]
                         }
                        """;

}
