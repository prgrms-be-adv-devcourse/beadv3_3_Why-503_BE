package io.why503.aiservice.domain.ai.service.impl;

import io.why503.aiservice.global.client.dto.response.Booking;
import io.why503.aiservice.global.client.dto.response.Performance;
import io.why503.aiservice.domain.ai.model.embedding.ShowCategory;
import io.why503.aiservice.domain.ai.model.embedding.genre.*;
import io.why503.aiservice.domain.ai.model.vo.*;
import io.why503.aiservice.domain.ai.service.AiService;
import io.why503.aiservice.domain.ai.service.mapper.*;
import io.why503.aiservice.global.client.PerformanceClient;
import io.why503.aiservice.global.client.ReservationClient;
import io.why503.aiservice.global.client.entity.mapper.BookingMapper;
import io.why503.aiservice.global.client.entity.mapper.PerformanceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * async
 * 추천과 검색을 하기 위해서는 텍스트를 숫자 벡터로 변환하여 유사도를 계산한다
 * 그러므로 공연 이름마다 임베딩 연산 처리 복잡, 추천 대상이 중복 연산 시 여러 번 계산 -> 응답 지연
 * 임베딩 성능 효율 올리고자 캐시 적용하는 방법으로 비동기 처리 -> 스레드 문제 해결 끝
 *
 * todomain
 * aiResponse -> resultResponse 타입 변환
 * ai용 추천 요약 문장, 설명 문장 포함 모든 정보  -> 사용자용 장르, 추천 이유

 * finalScore
 * 공연 정보에 따라 사용자의 취향에 맞는 공연을 가져와 일치하는지 확인
 * 사용자가 좋아하는 장르에 공연의 종류마다 점수 부여
 * 사용자의 취향에 추가적인 요소 적용값

 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final EmbeddingModel embeddingModel;
    private final ReservationClient reservationClient;
    private final PerformanceClient performanceClient;
    private final BookingMapper bookingMapper;
    private final PerformanceMapper performanceMapper;
    private final ShowGenreResolver genreResolver;
    private final AiChatImpl aiChat;
    private final ShowCalculatorImpl showCalculator;
    private final VectorSearchImpl vectorSearch;
    private final AiResponseMapper aiResponseMapper;
    private final ScoreResponseMapper scoreResponseMapper;
//    private final Executor aiExecutor;
    private final RecommendationsMapper recommendationsMapper;
    private final FallbackResultResponseMapper fallbackResultResponseMapper;


    //유사 공연
    public List<String> similarShows(List<TypeShowScore> topFinalShows, List<Performance> performances) {
        Set<String> similarShows = new LinkedHashSet<>();

        //각 장르마다 점수가 높은 점수 지정
        for (TypeShowScore show : topFinalShows) {
            performances.stream()
                    .filter(p -> p.category().equals(show.showCategory())
                            && !p.genre().equals(show.genre()))
                    .map(p -> p.genre())
                    .forEach(e -> similarShows.add(e));
        }
        return new ArrayList<>(similarShows);
    }



    //사용자의 행동 기반 상위 카테고리 결정
    public List<ShowCategory> topCategory(ResultRequest request, float[] userVector) {
        Map<ShowCategory, Double> scores = showCalculator.categoryScores(request, userVector);
        return scores.entrySet().stream()
                .sorted(Map.Entry.<ShowCategory, Double>comparingByValue().reversed())
                .map(categoryDoubleEntry -> categoryDoubleEntry.getKey())
                .limit(2).toList();
    }
    //상위 장르 결정
    public List<String> topGenre(ResultRequest request, Map<ShowCategory, Double> categoryScores) {
        if (request.genre() == null || request.genre().isEmpty()) return List.of();

        Map<String, Double> scores = showCalculator.genreScores(request, categoryScores);
        return scores.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .map(moodCategoryDoubleEntry -> moodCategoryDoubleEntry.getKey())
                .limit(2)
                .toList();
    }

    //구매한 내역 반환
    public ResultRequest tickets(List<Booking> bookings) {

        String showCategory = bookings.get(0).category();

        String genre = bookings.get(0).genre();

        return new ResultRequest(
                showCategory,
                genre
        );
    }



    //추천 받는 명령어 (프롬프트에 명령한 규칙 수행)
    public ResultResponse getRecommendations(Long userSq , ShowCategory showCategory, ShowGenre genre) {

        List<Booking> bookings =
                reservationClient.findMyBookings(userSq)
                        .stream()
                        .filter(bookingResponse -> "PAID".equals(bookingResponse.status()))
                        .map(bookingResponse -> bookingMapper.responseToBooking(bookingResponse))
                        .toList();
        bookings.forEach(i -> log.info("{}/{}",i.category(), i.genre()));

        List<Performance> performances =
                performanceClient.getShowCategoryGenre(showCategory, genre)
                        .stream()
                        .map(performanceResponse -> performanceMapper.responseToPerformance(performanceResponse))
                        .toList();



        ResultRequest ticketRequest = tickets(bookings);
        //임베딩 시작
        float[] userVector = vectorSearch.embed(ticketRequest, userSq);
        //선호하는 공연 선정 계산 ( aiResponse -> categoryScore )
        Map<ShowCategory, Double> categoryScores =
                showCalculator.categoryScores(ticketRequest, userVector);
        //선호하는 공연의 장르 선정 ( aiResponse -> genreScores )
        Map<String, Double> genreScores =
                showCalculator.genreScores(ticketRequest, categoryScores);
        //사용자의 상위 카테고리 데이터 ( aiResponse -> topCategory )
        List<ShowCategory> topShowCategory =
                topCategory(ticketRequest, userVector);

        //사용자의 상위 장르 데이터 ( topGenre )
        List<String> topGenre =
                topGenre(ticketRequest, categoryScores);
        //사용자의 최종 카테고리 공연 리스트
        List<Performance> topCategoryPerformances =
                performances.stream()
                        .filter(p -> topShowCategory.contains(p.category()))
                        .toList();

        //공연 장르 문서 검색
        List<Document> categoryDocs =
                vectorSearch.searchCategoryRules(topShowCategory);



        //CATEGORY 관련 문서 사용자 맞춤 규칙 검색
        Map<String, Document> categoryRule =
                categoryDocs.stream()
                        .collect(Collectors.toMap(
                                document -> document.getMetadata().get("showCategory").toString(),
                                document -> document,
                                (a, b) -> a
                        ));

        //공연 문서 검색
        List<Document> performanceDocs =
                vectorSearch.searchPerformances(topShowCategory);

        List<Performance> performancesVector =
                performanceDocs.stream()
                        .map(document -> performanceMapper.docToPerformance(document))
                        .filter(performance -> Objects.nonNull(performance))
                        .toList();

        //장르 점수 형식대로 출력 반환 ( ResultResponse -> categoryScore )
        Map<String, String> categoryScore =
                scoreResponseMapper.convertCategoryScore(categoryScores);
        //분위기 점수 형식대로 출력 반환 ( ResultResponse -> genreScore )
        Map<String, String> genreScore =
                scoreResponseMapper.convertGenreScore(genreScores);


        //공연 총합 순위 매김
        List<TypeShowScore> finalShows = showCalculator.finalShowRanking(
                        performancesVector,
                        categoryScores,
                        genreScores

                ).stream()
                .map(typeScore -> {
                    String showCategoryString = typeScore.showCategory();
                    String showGenre = typeScore.genre();

                    //카테고리 문서 기반 가중치 적용
                    Document ruleDoc = categoryRule.get(showCategoryString);
                    double ruleBoost = 0.0;
                    if (ruleDoc != null && ruleDoc.getText().contains(showGenre)) {
                        ruleBoost = 2.0;
                    }

                    double newScore = typeScore.typeScore() + ruleBoost;
                    return new TypeShowScore(showCategoryString, showGenre, newScore, typeScore.performance());
                })
                .sorted(Comparator.comparing((TypeShowScore typeShowScore) -> typeShowScore.typeScore()).reversed())
                .toList();
        List<TypeShowScore> topScoreShows = finalShows.stream()
                .limit(3)
                .toList();

        //공연 문서 기반 ( Recommendations 통합 처리 )
        List<Recommendations> performanceRecommendations = topScoreShows.stream()
                .map(TypeScore -> {
                    String showGenre = TypeScore.genre();
                    String showCategoryEnum = TypeScore.showCategory();


                    //카테고리 기반 가중치
                    String reason = "사용자 점수 기반 추천";
                    Document ruleDoc = categoryRule.get(showCategoryEnum);
                    if (ruleDoc != null && ruleDoc.getText().contains(showGenre)) {
                        reason += " + 카테고리 룰 기반 추천";
                    }



                    return new Recommendations(
                            showCategoryEnum,
                            reason,
                            showGenre
                    );
                })
                .toList();

        List<String> topShowCategories =
                topShowCategory.stream()
                        .map(category -> category.name())
                        .toList();

        List<String> topGenres = topGenre;

        // 장르 이름 리스트 ( Recommendations -> showCategory )
        List<String> topFinalShows = performanceRecommendations.stream()
                .map(recommendations -> recommendations.showGenre())
                .distinct()
                .toList();


        // 비슷한 공연 추천 ( ResultResponse -> similarShows )
        List<String> similarShows = similarShows(topScoreShows, performancesVector);

        //공연 장르 관련 리스트 ( ResultResponse -> similarTopShows )
        List<String> similarTopShows = topScoreShows.stream()
                .map(score -> score.genre())
                .distinct()
                .toList();

        String prompt = AiPrompt.prompt.formatted(
                performanceDocs,
                ticketRequest.showCategory(),
                ticketRequest.genre(),
                topShowCategory,
                topGenre,
                scoreResponseMapper.convertCategoryScore(categoryScores),
                scoreResponseMapper.convertGenreScore(genreScores),
                topScoreShows,
                similarShows
        );

        //ai에게 받는 응답을 문자열 파싱
        AiResponse aiResponse = aiChat.parse(aiChat.ask(prompt).join());

        //null 객체 처리
        AiResponse fixedResponse = aiResponseMapper.AiResponseToFixedResponse(
                aiResponse, categoryScore, genreScore, topShowCategories, topGenres, topFinalShows
        );

        //AiRecommendation -> 스트림, Recommendations -> 스트림
        List<Recommendations> finalRecommendations =
                fixedResponse.recommendations().stream()
                        .map(ar1 -> recommendationsMapper.toDomain(ar1))
                        .toList();


        //응답 반환
        return ResultResponseMapper.toResultResponse(
                fixedResponse,
                finalRecommendations,
                similarShows,
                similarTopShows
        );

    }


    public List<String> findSimilarShows(List<Recommendations> recommendations) {
        // fallback 예시
        if (recommendations == null || recommendations.isEmpty()) {
            return List.of();
        }

        // 간단 예: 추천 장르 이름 목록 반환
        return recommendations.stream()
                .map(r -> r.showGenre())
                .distinct()
                .toList();
    }
    //비슷한 장르 찾기
    public ResultResponse fallbackRecommendation(ResultRequest request, Long userSq) {

        List<Recommendations> fallbackRecommendations = List.of(
                new Recommendations(
                        ShowCategory.MUSICAL.name(),
                        "스토리와 음악이 뛰어난 뮤지컬",
                        ShowCategory.MUSICAL.getTypes().iterator().next().getName()
                ),
                new Recommendations(
                        ShowCategory.CONCERT.name(),
                        "라이브 공연 선호",
                        ShowCategory.CONCERT.getTypes().iterator().next().getName()
                ),
                new Recommendations(
                        ShowCategory.PLAY.name(),
                        "대화 중심 작품 선호",
                        ShowCategory.PLAY.getTypes().iterator().next().getName()
                ),
                new Recommendations(
                        ShowCategory.CLASSIC.name(),
                        "차분한 분위기, 클래식 선호",
                        ShowCategory.CLASSIC.getTypes().iterator().next().getName()
                )
        );

        float[] userVector;
        try {
            userVector = vectorSearch.embed(request, userSq);
        } catch (Exception e) {
            userVector = new float[embeddingModel.dimensions()];
        }

        Map<ShowCategory, Double> categoryScores = showCalculator.categoryScores(request, userVector);
        Map<String, Double> genreScores = showCalculator.genreScores(request, categoryScores);

        Map<String, String> categoryScore = scoreResponseMapper.convertCategoryScore(categoryScores);
        Map<String, String> genreScore = scoreResponseMapper.convertGenreScore(genreScores);

        List<String> topCategory = topCategory(request, userVector).stream().map(category -> category.name()).toList();

        List<String> topGenre = topGenre(request, categoryScores);

        List<String> topFinalShows = fallbackRecommendations.stream()
                .map(r -> r.showGenre())
                .toList();

        List<String> similarShows = findSimilarShows(fallbackRecommendations);

        List<String> similarTopShows = fallbackRecommendations.stream()
                .map(r -> r.showGenre())
                .distinct().limit(3)
                .toList();

        // Mapper 사용
        return fallbackResultResponseMapper.toResultResponse(
                fallbackRecommendations,
                categoryScore,
                genreScore,
                topCategory,
                topGenre,
                topFinalShows,
                similarShows,
                similarTopShows
        );
    }
}
