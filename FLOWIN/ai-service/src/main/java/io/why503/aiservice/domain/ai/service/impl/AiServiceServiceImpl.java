package io.why503.aiservice.domain.ai.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.why503.aiservice.domain.ai.model.embedding.Booking;
import io.why503.aiservice.domain.ai.model.embedding.Performance;
import io.why503.aiservice.domain.ai.model.embedding.ShowCategory;
import io.why503.aiservice.domain.ai.model.embedding.genre.*;
import io.why503.aiservice.domain.ai.model.vo.*;
import io.why503.aiservice.domain.ai.service.AiService;
import io.why503.aiservice.domain.ai.service.mapper.AiResponseMapper;
import io.why503.aiservice.domain.ai.service.mapper.FallbackResultResponseMapper;
import io.why503.aiservice.domain.ai.service.mapper.RecommendationsMapper;
import io.why503.aiservice.domain.ai.service.mapper.ResultResponseMapper;
import io.why503.aiservice.global.client.PerformanceClient;
import io.why503.aiservice.global.client.ReservationClient;
import io.why503.aiservice.global.client.entity.mapper.BookingMapper;
import io.why503.aiservice.global.client.entity.mapper.PerformanceMapper;
import io.why503.aiservice.global.exception.AiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
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
@EnableAsync
public class AiServiceServiceImpl implements AiService {

    private final ChatClient chatClient;
    private final ObjectMapper mapper;
    private final VectorStore vectorStore;
    private final EmbeddingModel embeddingModel;
    private final ReservationClient reservationClient;
    private final PerformanceClient performanceClient;
    private final BookingMapper bookingMapper;
    private final PerformanceMapper performanceMapper;
    private final Performance performance;
    private final ShowGenreResolver genreResolver;

    //사용자가 이 문자열 입력에 의해 임베딩 모델 학습 (텍스트 -> 숫자) / float []
    public float[] embed(ResultRequest request, Long userSq) {

        List<Booking> bookings =
                reservationClient.findMyBookings(userSq)
                        .stream()
                        .filter(bookingResponse -> "PAID".equals(bookingResponse.status()))
                        .map(bookingResponse -> bookingMapper.BookingResponseToBooking(bookingResponse))
                        .toList();

        List<ShowCategory> categories = bookings.stream()
                .filter(b -> "PAID".equalsIgnoreCase(b.status()))
                .map(booking -> ShowCategory.valueOf(booking.category()))
                .toList();

        List<ShowGenre> genres = bookings.stream()
                .filter(b -> "PAID".equalsIgnoreCase(b.status()))
                .map(booking -> {
                    switch (ShowCategory.valueOf(booking.category())) {
                        case MUSICAL:
                            return MusicalType.fromString(booking.genre());
                        // 다른 카테고리의 장르는 각각 다른 구현체에서 fromString 호출
                        case CONCERT:
                            return ConcertType.fromString(booking.genre());
                        case PLAY:
                            return PlayType.fromString(booking.genre());
                        default:
                            throw new IllegalArgumentException("Unknown category: " + booking.category());
                    }
                })
                .toList();

        String showCategory = categories.stream()
                .map(Category -> Category.name())
                .collect(Collectors.joining(","));

        String genre = genres.stream()
                .map(Genre -> Genre.getName())
                .collect(Collectors.joining(","));

        String text = """
                      사용자의 공연 선호 정보입니다.
                      구매한 카테고리: %s
                      구매한 장르:%s
                      """.formatted(
                showCategory,
                genre
                      );
        return embeddingModel.embed(text);
    }

    //두 백터 간 코사인 유사도 계산
    public double cosineSimilarity(float[] vectorA, float[] vectorB) {
        //같은 벡터 비교
        if ( vectorA.length != vectorB.length || vectorA.length == 0 ) {
            return 0.0;
        }
        double dotProduct = 0.0;
        double magnitudeA = 0.0;
        double magnitudeB = 0.0;

        for ( int i = 0; i < vectorA.length; i++ ) {
            dotProduct += vectorA[i] * vectorB[i];
            magnitudeA += vectorA[i] * vectorA[i];
            magnitudeB += vectorB[i] * vectorB[i];
        }
        //유사도 값에 따라 비슷한 의미를 나옴
        return dotProduct / (Math.sqrt(magnitudeA) * Math.sqrt(magnitudeB));
    }

    //장르 점수 계산 (빈도 + 임베딩 유사도)
    public Map<ShowCategory, Double> CategoryScores(ResultRequest request, float[] userVector) {
        Map<ShowCategory, Double> scoreMap = new EnumMap<>(ShowCategory.class);

        //구매 횟수 (가중치 적용하기 위해 장르 중복 발생 시 로그 처리)
        Map<ShowCategory, Long> categoryCount =
                request.showCategory().stream()
                .collect(Collectors.groupingBy(c -> c, Collectors.counting()));


        //로그 + 가중치 (중복 발생 시 로그 적용한 다음에 작은 수로 비율 적용)
        categoryCount.forEach((showCategory, count) -> {
            double score = Math.log1p(count) * 3.0;
            scoreMap.merge(showCategory, score, (a, b) -> Double.sum(a, b));
        });


        //showCategory 문서 텍스트를 필드 캐시 적용
        Map<String, float[]> embedCache = new ConcurrentHashMap<>();
        // 각 장르별로 유사도 계산 (장르마다 점수 부여과 점수 총합 검색)
        for (ShowCategory c : ShowCategory.values()) {
            List<Document> docs =
                    vectorStore.similaritySearch(
                    SearchRequest.builder().query(c.name()).topK(3).build()
            ).stream().filter(d -> "CATEGORY".equals(d.getMetadata().get("type")))
                    .toList();

            //벡터로 처리한 리스트별로 장르마다 높은 수로 지정함 (서로 비슷한 점수 간격 벌리기)
            double maxSim =
                    docs.stream()
                    .map(document -> {
                        float[] categoryVector = embedCache.computeIfAbsent(
                                document.getText(),
                                text -> embeddingModel.embed(text)
                        );
                        return cosineSimilarity(userVector, categoryVector);
                    })
                    .sorted(Comparator.reverseOrder()).limit(2)
                    .mapToDouble(Double -> Double.doubleValue())
                    .average().orElse(0.0);
            scoreMap.merge(c, maxSim * 5, (a, b) -> Double.sum(a, b));
            };

            //결과값을 보기 위한 백분위 처리
            double percent100 = 100.0;
            double total = scoreMap.values()
                .stream()
                .mapToDouble(Double -> Double.doubleValue())
                .sum();

        Map<ShowCategory, Double> percentScoreMap = new EnumMap<>(ShowCategory.class);
        for (Map.Entry<ShowCategory, Double> entry : scoreMap.entrySet()) {
            double percent = total == 0
                    ? 0.0
                    : (entry.getValue() / total) * percent100;
            percentScoreMap.put(entry.getKey(), percent);
        }
        return percentScoreMap;
    }



    //Category 점수를 ShowCategory에 분배
    public Map<ShowGenre, Double> GenreScores(ResultRequest request, Map<ShowCategory, Double> categoryScores) {

        Map<ShowGenre, Double> scoreMap = new HashMap<>();

        //사용자가 선택한 장르 직접 가중치
        if (request.genre() != null) {
            request.genre().forEach(Genre ->
                    scoreMap.merge(Genre, 3.0, (a, b) -> Double.sum(a, b))
            );
        }

        for (ShowCategory showCategory : ShowCategory.values()) {
            double categoryScore = categoryScores.getOrDefault(showCategory, 0.0);

            for (ShowGenre show : showCategory.getTypes()) {
                scoreMap.merge(show, categoryScore * 0.5, (a, b) -> Double.sum(a, b));
            }
        }

        double percent100 = 100.0;
        double total = scoreMap.values().stream()
                .mapToDouble(Double -> Double.doubleValue())
                .sum();

        Map<ShowGenre, Double> percentMap = new HashMap<>();

        for (Map.Entry<ShowGenre, Double> entry : scoreMap.entrySet()) {
            double percent = total == 0
                    ? 0.0
                    : (entry.getValue() / total) * percent100;
            percentMap.put(entry.getKey(), percent);
        }

        return percentMap;
    }



    //최종 점수 = 카테고리 점수 50% + 분위기 점수 합 50% ( 최종 추천에 대한 점수 계산 )
    public double FinalScore(
            Performance performance,
            Map<ShowCategory, Double> categoryScores,
            Map<ShowGenre, Double> genreScores
    ) {

        //set -> 리스트별로 하나씩 찾아야 함 / map : 문자 길이나 특정한 키값으로 통해 얻음
        //set 으로 처리해야 하지만 스트림으로 대신하여 미리 map 처리 (getOrDefault 문서)
        double categoryScore =
                categoryScores.getOrDefault(performance.category(), 0.0);
        double genreScore =
                genreScores.getOrDefault(performance.genre(), 0.0);

        log.info("categoryScore: {}, genreScore: {}", categoryScore, genreScore);
        return categoryScore * 0.5
                + genreScore * 0.5;
    }


    //상위 장르 + 점수 -> 매김
    public List<TypeShowScore> FinalShowRanking(
            List<Performance> performances,
            Map<ShowCategory, Double> categoryScores,
            Map<ShowGenre, Double> genreScores
    ) {
        List<TypeShowScore> results = new ArrayList<>();

        //공연의 점수를 지정된 것들을 포장
        for (Performance performance : performances) {
                double score = FinalScore(performance, categoryScores,genreScores);
                results.add(new TypeShowScore(performance.category(), performance.genre(), score, performance));

        }
        results.sort(Comparator.comparing((TypeShowScore typeShowScore) -> typeShowScore.typeScore()).reversed());

        return results;
    }


    //유사 공연
    public List<String> SimilarShows(List<TypeShowScore> topFinalShows, List<Performance> performances) {
        Set<String> similarShows = new LinkedHashSet<>();

        //각 장르마다 점수가 높은 점수 지정
        for (TypeShowScore Show : topFinalShows) {
            ShowCategory showCategory = Show.showCategory();
            ShowGenre topShowType = Show.genre();


            performances.stream()
                    .filter(performance -> performance.category().equals(showCategory)
                            && !performance.genre().equals(topShowType));
                    }
        return new ArrayList<>(similarShows);
    }



    //사용자의 행동 기반 상위 카테고리 결정
    public List<ShowCategory> TopCategory(ResultRequest request, float[] userVector) {
        Map<ShowCategory, Double> scores = CategoryScores(request, userVector);
        return scores.entrySet().stream()
                .sorted(Map.Entry.<ShowCategory, Double>comparingByValue().reversed())
                .map(categoryDoubleEntry -> categoryDoubleEntry.getKey())
                .limit(2).toList();
    }
    //상위 장르 결정
    public List<ShowGenre> TopGenre(ResultRequest request, Map<ShowCategory, Double> categoryScores) {
        if (request.genre() == null || request.genre().isEmpty()) return List.of();

        Map<ShowGenre, Double> scores = GenreScores(request, categoryScores);
        return scores.entrySet().stream()
                .sorted(Map.Entry.<ShowGenre, Double>comparingByValue().reversed())
                .map(moodCategoryDoubleEntry -> moodCategoryDoubleEntry.getKey())
                .limit(2)
                .toList();
    }


    //ai 프롬프트 보냄(AI서버) -> 문자열 응답식으로 받음
    public CompletableFuture<String> ask(String prompt) {
        try {
            return CompletableFuture.supplyAsync(() -> chatClient.prompt(prompt).call().content());

        } catch (Exception e) {
            log.error("ai 호출 실패", e);
            return CompletableFuture.failedFuture(e);
        }
    }
    //포스트맨에서 json 형식으로 받기 위한 파싱
    private AiResponse parse(String content) {
        try { return mapper.readValue(cleanJson(content), AiResponse.class);
        } catch (Exception e) { log.error("AI 응답 파싱 실패. content={}", content, e);
            throw AiException.invalidResponse();
        }
    }
    //출력에 빈 리스트를 받지 않기 위한 json에 불필요한 정보 지우기
    public String cleanJson(String content) {
        if (content == null) return "{}";
        int start = content.indexOf("{");
        int end = content.lastIndexOf("}");
        return (start >= 0 && end > start) ? content.substring(start, end + 1) : "{}";
    }


    //문자열 ai용 추천 반환
    public Recommendations toDomain(AiRecommendation ar) {
        ShowCategory showCategory = ShowCategory.fromString(ar.showCategory());
        ShowGenre showGenre = genreResolver.fromString(ar.showGenre());
        if (!showCategory.supports(showGenre)) {
            throw AiException.invalidGenre();
        }

        return new Recommendations(
                showCategory,
                ar.reason(),
                showGenre
        );
    }

    //점수 반환
    public Map<String, String> convertCategoryScore(Map<ShowCategory, Double> scores) {
        return scores.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().name(),
                        entry ->  String.format("%.2f...", entry.getValue())
                ));
    }

    //점수 반환
    public Map<String, String> convertGenreScore(Map<ShowGenre, Double> scores) {
        return scores.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().getName(),
                        entry -> String.format("%.2f...", entry.getValue())
                ));
    }

    //장르 문서 검색용 함수
    public List<Document> searchCategoryRules(List<ShowCategory> topShowCategory) {
        String query = topShowCategory.stream()
                .map(showCategory -> showCategory.name())
                .collect(Collectors.joining(" "));

        return vectorStore.similaritySearch(
                        SearchRequest.builder()
                                .query(query)
                                .topK(5)
                                .build()
                ).stream()
                .filter(d -> "CATEGORY".equals(d.getMetadata().get("type")))
                .toList();
    }

    //공연 문서 검색용 함수
    public List<Document> searchPerformances(List<ShowCategory> topShowCategory) {
        return vectorStore.similaritySearch(
                        SearchRequest.builder()
                                .query("공연")
                                .topK(50)
                                .build()
                ).stream()
                .filter(d -> "PERFORMANCE".equals(d.getMetadata().get("type")))
                .toList();
    }

    //구매한 내역 반환
    public ResultRequest Tickets(List<Booking> bookings) {

        List<ShowCategory> showCategory = bookings.stream()
                .map(booking -> ShowCategory.valueOf(booking.category()))
                .toList();

        List<ShowGenre> genre = bookings.stream()
                .map(booking -> genreResolver.fromString(booking.genre()))
                .toList();

        return new ResultRequest(
                showCategory,
                genre

        );
    }



    //추천 받는 명령어 (프롬프트에 명령한 규칙 수행)
    public  CompletableFuture<ResultResponse> getRecommendations(ResultRequest request, Long userSq , ShowCategory showCategory, ShowGenre genre) {

        try {
            return CompletableFuture.supplyAsync(() -> {


                List<Booking> bookings =
                        reservationClient.findMyBookings(userSq)
                                .stream()
                                .filter(bookingResponse -> "PAID".equals(bookingResponse.status()))
                                .map(bookingResponse -> bookingMapper.BookingResponseToBooking(bookingResponse))
                                .toList();


                List<Performance> performances =
                        performanceClient.getShowCategoryGenre(showCategory, genre)
                                .stream()
                                .map(performanceResponse -> performanceMapper.PerformanceResponseToPerformance(performanceResponse))
                                .toList();



                ResultRequest ticketRequest = Tickets(bookings);
                //임베딩 시작
                float[] userVector = embed(ticketRequest, userSq);
                //선호하는 공연 선정 계산 ( aiResponse -> categoryScore )
                Map<ShowCategory, Double> categoryScores =
                        CategoryScores(ticketRequest, userVector);
                //선호하는 공연의 장르 선정 ( aiResponse -> genreScores )
                Map<ShowGenre, Double> genreScores =
                        GenreScores(ticketRequest, categoryScores);
                //사용자의 상위 카테고리 데이터 ( aiResponse -> topCategory )
                List<ShowCategory> topShowCategory =
                        TopCategory(ticketRequest, userVector);

                //사용자의 상위 장르 데이터 ( topGenre )
                List<ShowGenre> topGenre =
                        TopGenre(ticketRequest, categoryScores);
                //사용자의 최종 카테고리 공연 리스트
                List<Performance> topCategoryPerformances =
                        performances.stream()
                                .filter(p -> topShowCategory.contains(p.category()))
                                .toList();

                //공연 장르 문서 검색
                List<Document> categoryDocs =
                        searchCategoryRules(topShowCategory);



                //CATEGORY 관련 문서 사용자 맞춤 규칙 검색
                Map<ShowCategory, Document> categoryRule =
                        categoryDocs.stream()
                                .collect(Collectors.toMap(
                                        document -> ShowCategory.valueOf(document.getMetadata().get("showCategory").toString()),
                                        document -> document,
                                        (a, b) -> a
                                ));

                //공연 문서 검색
                List<Document> performanceDocs =
                        searchPerformances(topShowCategory);

                List<Performance> performancesVector =
                        performanceDocs.stream()
                                .map(document -> performance.toPerformance(document))
                                .filter(performance -> Objects.nonNull(performance))
                                .toList();

                //장르 점수 형식대로 출력 반환 ( ResultResponse -> categoryScore )
                Map<String, String> CategoryScore =
                        convertCategoryScore(categoryScores);
                //분위기 점수 형식대로 출력 반환 ( ResultResponse -> genreScore )
                Map<String, String> GenreScore =
                        convertGenreScore(genreScores);


                //공연 총합 순위 매김
                List<TypeShowScore> finalShows = FinalShowRanking(
                        performancesVector,
                        categoryScores,
                        genreScores

                ).stream()
                        .map(typeScore -> {
                            ShowCategory showCategoryEnum = typeScore.showCategory();
                            ShowGenre showGenre = typeScore.genre();

                            //카테고리 문서 기반 가중치 적용
                            Document ruleDoc = categoryRule.get(showCategoryEnum);
                            double ruleBoost = 0.0;
                            if (ruleDoc != null && ruleDoc.getText().contains(showGenre.getName())) {
                                ruleBoost = 2.0;
                            }

                            double newScore = typeScore.typeScore() + ruleBoost;
                            return new TypeShowScore(showCategoryEnum, showGenre, newScore, typeScore.performance());
                        })
                        .sorted(Comparator.comparing((TypeShowScore typeShowScore) -> typeShowScore.typeScore()).reversed())
                        .toList();
                List<TypeShowScore> topScoreShows = finalShows.stream()
                        .limit(3)
                        .toList();

                //공연 문서 기반 ( Recommendations 통합 처리 )
                List<Recommendations> performanceRecommendations = topScoreShows.stream()
                        .map(TypeScore -> {
                            ShowGenre showGenre = TypeScore.genre();
                            ShowCategory showCategoryEnum = TypeScore.showCategory();


                            //카테고리 기반 가중치
                            String reason = "사용자 점수 기반 추천";
                            Document ruleDoc = categoryRule.get(showCategoryEnum);
                            if (ruleDoc != null && ruleDoc.getText().contains(showGenre.getName())) {
                                reason += " + 카테고리 룰 기반 추천";
                            }



                            return new Recommendations(
                                    showCategoryEnum,
                                    reason,
                                    showGenre
                            );
                        })
                        .toList();

                // 장르 이름 리스트 ( Recommendations -> showCategory )
                List<String> topFinalShows = performanceRecommendations.stream()
                        .map(recommendations -> recommendations.showGenre().getName())
                        .toList();


                // 비슷한 공연 추천 ( ResultResponse -> similarShows )
                List<String> similarShows = SimilarShows(topScoreShows, performancesVector);

                //공연 장르 관련 리스트 ( ResultResponse -> similarTopShows )
                List<String> similarTopShows = topScoreShows.stream()
                        .map(score -> score.genre().getName())
                        .distinct()
                        .toList();

                String prompt = AiServicePromptImpl.prompt.formatted(
                        categoryRule,
                        performanceDocs,
                        ticketRequest.showCategory(),
                        ticketRequest.genre(),
                        topShowCategory,
                        topGenre,
                        convertCategoryScore(categoryScores),
                        convertGenreScore(genreScores),
                        topScoreShows,
                        topFinalShows,
                        similarShows
                );

                //ai에게 받는 응답을 문자열 파싱
                AiResponse aiResponse = parse(ask(prompt).join());

                //null 객체 처리
                AiResponse fixedResponse = AiResponseMapper.toFixedResponse(
                        aiResponse, categoryScores, genreScores, topShowCategory, topGenre, finalShows
                );

                //AiRecommendation -> 스트림, Recommendations -> 스트림
                List<Recommendations> finalRecommendations = RecommendationsMapper.toAiRecommendations(fixedResponse.recommendations());


                //응답 반환
                return ResultResponseMapper.toResultResponse(
                        fixedResponse,
                        finalRecommendations,
                        similarShows,
                        similarTopShows
                );

            });
        } catch (Exception e) {
            return CompletableFuture.completedFuture(fallbackRecommendation(request, userSq));
        }

    }

    @Override
    public List<String> findSimilarShows(List<Recommendations> recommendations) {
        // fallback 예시
        if (recommendations == null || recommendations.isEmpty()) {
            return List.of();
        }

        // 간단 예: 추천 장르 이름 목록 반환
        return recommendations.stream()
                .map(r -> r.showGenre().getName())
                .distinct()
                .toList();
    }
    //비슷한 장르 찾기
    public ResultResponse fallbackRecommendation(ResultRequest request, Long userSq) {

        List<Recommendations> fallbackRecommendations = List.of(
                new Recommendations(
                        ShowCategory.MUSICAL,
                        "스토리와 음악이 뛰어난 뮤지컬",
                        ShowCategory.MUSICAL.getTypes().iterator().next()
                ),
                new Recommendations(
                        ShowCategory.CONCERT,
                        "라이브 공연 선호",
                        ShowCategory.CONCERT.getTypes().iterator().next()
                ),
                new Recommendations(
                        ShowCategory.PLAY,
                        "대화 중심 작품 선호",
                        ShowCategory.PLAY.getTypes().iterator().next()
                ),
                new Recommendations(
                        ShowCategory.CLASSIC,
                        "차분한 분위기, 클래식 선호",
                        ShowCategory.CLASSIC.getTypes().iterator().next()
                )
        );

        float[] userVector;
        try {
            userVector = embed(request, userSq);
        } catch (Exception e) {
            userVector = new float[embeddingModel.dimensions()];
        }

        Map<ShowCategory, Double> categoryScores = CategoryScores(request, userVector);
        Map<ShowGenre, Double> genreScores = GenreScores(request, categoryScores);

        Map<String, String> categoryScore = convertCategoryScore(categoryScores);
        Map<String, String> genreScore = convertGenreScore(genreScores);
        List<String> topCategory = TopCategory(request, userVector).stream().map(ShowCategory::name).toList();
        List<String> topGenre = TopGenre(request, categoryScores).stream().map(ShowGenre::getName).toList();
        List<String> topFinalShows = fallbackRecommendations.stream()
                .map(r -> r.showGenre().getName())
                .toList();
        List<String> similarShows = findSimilarShows(fallbackRecommendations);
        List<String> similarTopShows = fallbackRecommendations.stream()
                .map(r -> r.showGenre().getName())
                .distinct().limit(3)
                .toList();

        // Mapper 사용
        return FallbackResultResponseMapper.toResultResponse(
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
