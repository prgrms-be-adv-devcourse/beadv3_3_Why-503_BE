package io.why503.aiservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.why503.aiservice.model.embedding.*;
import io.why503.aiservice.model.vo.*;
import io.why503.aiservice.repository.PerformanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableAsync
public class AiService {

    private final ChatClient chatClient;
    private final ObjectMapper mapper;
    private final VectorStore vectorStore;
    private final EmbeddingModel embeddingModel;
    private final List<Performance> performances;
    private final PerformanceRepository performanceRepository;

    /**
     * 추천과 검색을 하기 위해서는 텍스트를 숫자 벡터로 변환하여 유사도를 계산한다
     * 그러므로 임베딩 연산 처리 복잡, 추천 대상이 중복 연산 시 여러 번 계산 -> 응답 지연
     * 임베딩 성능 효율 올리고자 캐시 적용하는 방법으로 비동기 처리 -> 스레드 문제 해결 끝
     */
    // 임베딩 캐시 (텍스트 -> float[])
    private final Map<String, float[]> Cache = new ConcurrentHashMap<>();
    // 비동기 임베딩 호출
    @Async
    public CompletableFuture<float[]> async(String text) {
        return CompletableFuture.completedFuture(getCache(text));
    }
    // 캐시 적용 임베딩
    private float[] getCache(String text) {
        return Cache.computeIfAbsent(text, t -> embeddingModel.embed(t));
    }
    public CompletableFuture<Map<String, float[]>> asyncPerformances(List<Performance> performanceList) {
        Map<String, float[]> resultMap = new ConcurrentHashMap<>();
        List<CompletableFuture<Void>> futures = performanceList.stream()
                .map(performance -> async(performance.name()).thenAccept(vec -> resultMap.put(performance.name(), vec)))
                .toList();
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> resultMap);
    }


    //사용자가 이 문자열 입력에 의해 임베딩 모델 학습 (텍스트 -> 숫자) / float []
    public float[] embed(ResultRequest request) {
        String text = """
                      사용자의 공연 선호 정보입니다.
                      최근 구매한 장르: %s
                      관심 있는 장르: %s
                      선호 무드: %s
                      """.formatted(
                request.category(),
                request.attention(),
                request.mood() != null ? request.mood() : List.of()
                      );
        return embeddingModel.embed(text);
    }
    //두 백터 간 코사인 유사도 계산
    public double cosineSimilarity(float[] vectorA, float[] vectorB) {
        //같은 벡터 비교
        if ( vectorA.length != vectorB.length ) { throw new IllegalArgumentException("Vectors must be of equal length");
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
    private Map<Category, Double> CategoryScores(ResultRequest request) {
        Map<Category, Double> scoreMap = new EnumMap<>(Category.class);

        //구매 횟수 (가중치 적용하기 위해 장르 중복 발생 시 로그 처리)
        Map<Category, Long> categoryCount = request.category().stream()
                .collect(Collectors.groupingBy(c -> c, Collectors.counting()));
        Map<Category, Long> attentionCount = request.attention().stream()
                .collect(Collectors.groupingBy(c -> c, Collectors.counting()));

        //로그 + 가중치 (중복 발생 시 로그 적용한 다음에 작은 수로 비율 적용)
        categoryCount.forEach((category, count) -> {
            double score = Math.log1p(count) * 3.0;
            scoreMap.merge(category, score, (a, b) -> Double.sum(a, b));
        });
        attentionCount.forEach((category, count) -> {
            double score = Math.log1p(count) * 1.0;
            scoreMap.merge(category, score, (a, b) -> Double.sum(a, b));
        });

        // 사용자 행동을 임베딩 (사용자의 구매 행위에 따라 가중치 부여)
        float[] userVector = embed(request);

        // 각 장르별로 유사도 계산 (장르마다 점수 부여과 점수 총합 검색)
        for (Category c : Category.values()) {
            List<Document> docs = vectorStore.similaritySearch(
                    SearchRequest.builder().query(c.name()).topK(3).build()
            ).stream()
                    .filter(d -> "CATEGORY".equals(d.getMetadata().get("type")))
                    .toList();

            //벡터로 처리한 리스트별로 장르마다 높은 수로 지정함 (서로 비슷한 점수 간격 벌리기)
            double maxSim = docs.stream()
                    .map(doc -> cosineSimilarity(userVector, embeddingModel.embed(
                            doc.getText().toString())))
                    .sorted(Comparator.reverseOrder())
                    .limit(2)
                    .mapToDouble(Double -> Double.doubleValue())
                    .average()
                    .orElse(0.0);
            scoreMap.merge(c, maxSim * 5, (a, b) -> Double.sum(a, b));
            };

            //결과값을 보기 위한 백분위 처리
            double percent100 = 100.0;
            double total = scoreMap.values()
                .stream()
                .mapToDouble(Double -> Double.doubleValue())
                .sum();

        Map<Category, Double> percentScoreMap = new EnumMap<>(Category.class);
        for (Map.Entry<Category, Double> entry : scoreMap.entrySet()) {
            double percent = total == 0
                    ? 0.0
                    : (entry.getValue() / total) * percent100;
            percentScoreMap.put(entry.getKey(), percent);
        }
        return percentScoreMap;
    }


    //사용자가 특정한 취향을 맞추기 위한 점수 계산
    private Map<MoodCategory, Double> MoodScores(ResultRequest request) {
        if (request.mood() == null || request.mood().isEmpty()) return Map.of();
        Map<MoodCategory, Double> scoreMap = new EnumMap<>(MoodCategory.class);

        // 기본 점수
        for (MoodCategory m : request.mood())
            scoreMap.merge( m, 1.0, (a, b) -> Double.sum(a, b));

        // 임베딩 기반 유사도
        float[] moodVector = embeddingModel.embed(
                String.join(" ", request.mood().stream().map(
                        moodCategory -> moodCategory.name()).toList())
        );

        for (MoodCategory m : MoodCategory.values()) {
            List<Document> docs = vectorStore.similaritySearch(
                    SearchRequest.builder().query(m.name()).topK(3).build()
            );

            double maxSim = docs.stream()
                    .map(doc -> cosineSimilarity(
                            moodVector, embeddingModel.embed(doc.getText().toString())))
                    .max((a, b) -> Double.compare(a, b))
                    .orElse(0.0);
            scoreMap.merge(m, maxSim * 5, (a, b) -> Double.sum(a, b));
        }
            double percent100 = 100.0;
            double total = scoreMap.values()
                    .stream()
                    .mapToDouble(Double -> Double.doubleValue())
                    .sum();

            Map<MoodCategory, Double> percentScoreMap = new EnumMap<>(MoodCategory.class);

            for (Map.Entry<MoodCategory, Double> entry : scoreMap.entrySet()) {
                double percent = total == 0
                        ? 0.0
                        : (entry.getValue() / total) * percent100;

                percentScoreMap.put(entry.getKey(), percent);

            }
        return percentScoreMap;
    }

    /**
     * @param performance
     * 공연 정보에 따라 사용자의 취향에 맞는 공연을 가져와 일치하는지 확인
     * @param categoryScores
     * 사용자가 좋아하는 장르에 공연의 종류마다 점수 부여
     * @param moodScores
     * 사용자의 취향에 추가적인 요소 적용값
     * @return
     */
    //최종 점수 = 카테고리 점수 50% + 분위기 점수 합 50% ( 최종 추천에 대한 점수 계산 )
    private double FinalScore(
            Performance performance,
            Map<Category, Double> categoryScores,
            Map<MoodCategory, Double> moodScores
    ) {
        /**
         * set -> 리스트별로 하나씩 찾아야 함 /
         * map :  문자 길이나 특정한 키값으로 통해 얻음
         * 이러므로 탐색 횟수 줄어 반복 줄인다
         */
        //set 으로 처리해야 하지만 스트림으로 대신하여 미리 map 처리
        double categoryScore =
                categoryScores.getOrDefault(performance.getCategory(), 0.0);
        double moodScore =
                performance.getMoods().stream()
                        .mapToDouble(moodCategory -> moodScores.getOrDefault(moodCategory, 0.0))
                        .average()
                        .orElse(0.0);

        return categoryScore * 0.5
                + moodScore * 0.5;
    }


    //상위 장르 + 점수 -> 매김
    private List<TypeShowScore> FinalShowRanking(
            List<Performance> performances,
            List<Performance> performancesVector, Map<Category, Double> categoryScores,
            Map<MoodCategory, Double> moodScores
    ) {
        List<TypeShowScore> results = new ArrayList<>();

        //공연의 점수를 지정된 것들을 포장
        for (Performance performance : performances) {
            for (ShowCategory show : performance.category().getTypes()) {
                double score = FinalScore(performance, categoryScores, moodScores);
                results.add(new TypeShowScore(performance.category(), show, score, performance));
            }
        }
        return results.stream()
                .toList();
    }


    private List<String> SimilarShows(List<TypeShowScore> topFinalShows, List<Performance> performances) {
        Set<String> similarShows = new LinkedHashSet<>();

        for (TypeShowScore topShow : topFinalShows) {
            Category category = topShow.category();
            ShowCategory topShowType = topShow.showType();
            Set<MoodCategory> topMoods = topShowType.moods();

            List<Performance> candidates = performances.stream()
                    .filter(performance -> performance.getCategory().equals(category) && !performance.genre().equals(topShowType))
                    .toList();

            Map<Performance, Long> score = new HashMap<>();
            for (Performance candidate : candidates) {
                long commonMood = candidate.genre().moods().stream()
                        .filter(moodCategory -> topMoods.contains(moodCategory))
                        .count();
                score.put(candidate, commonMood);
            }

            score.entrySet().stream()
                    .sorted(Map.Entry.<Performance, Long>comparingByValue().reversed())
                    .map(entry -> entry.getKey().name())
                    .limit(2)
                    .forEach(entry -> similarShows.add(entry));
        }

        return new ArrayList<>(similarShows);
    }



    // 사용자의 행동 기반 상위 장르 결정
    private List<Category> TopCategory(ResultRequest request) {
        Map<Category, Double> scores = CategoryScores(request);
        return scores.entrySet().stream()
                .sorted(Map.Entry.<Category, Double>comparingByValue().reversed())
                .map(categoryDoubleEntry -> categoryDoubleEntry.getKey())
                .limit(2)
                .toList();
    }
    private List<MoodCategory> TopMoods(ResultRequest request) {
        if (request.mood() == null || request.mood().isEmpty()) return List.of();

        Map<MoodCategory, Double> scores = MoodScores(request);
        return scores.entrySet().stream()
                .sorted(Map.Entry.<MoodCategory, Double>comparingByValue().reversed())
                .map(moodCategoryDoubleEntry -> moodCategoryDoubleEntry.getKey())
                .limit(2)
                .toList();
    }


    /**
     * ai 호출 처리용, 파싱용, 응답 처리용
     * @param prompt
     * @return
     */
    //ai 프롬프트 보냄(AI서버) -> 문자열 응답식으로 받음
    public String ask(String prompt) {
        return chatClient.prompt(prompt).call().content();
    }
    //포스트맨에서 json 형식으로 받기 위한 파싱
    public AiResponse parse(String content) {
        try { return mapper.readValue(cleanJson(content), AiResponse.class);
        } catch (Exception e) { log.error("AI 응답 파싱 실패. content={}", content, e);
            throw new IllegalStateException("AI 응답 파싱 실패");
        }
    }
    //출력에 빈 리스트를 받지 않기 위한 json에 불필요한 정보 지우기
    private String cleanJson(String content) {
        if (content == null) return "{}";
        int start = content.indexOf("{");
        int end = content.lastIndexOf("}");
        return (start >= 0 && end > start) ? content.substring(start, end + 1) : "{}";
    }

    /**
     * aiResponse -> resultResponse 타입 변환
     * ai용 추천 요약 문장, 설명 문장 포함 모든 정보  -> 사용자용 장르, 추천 이유
     */
    private Recommendations toDomain(AiRecommendation ar, Map<MoodCategory, Double> moodScores) {
        Category category = Category.fromString(ar.category()).orElse(null);
        ShowCategory showCategory = Objects.requireNonNull(category).findShowType(ar.showCategory());
        Set<MoodCategory> mood = showCategory.pickMood(showCategory, moodScores);
        return new Recommendations(category, ar.reason(), mood, showCategory,null,null,null,null );
    }

    //점수 반환
    private Map<String, String> convertCategoryScore(Map<Category, Double> scores) {
        return scores.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().name(),
                        entry ->  String.format("%.2f...", entry.getValue())
                ));
    }

    //점수 반환
    private Map<String, String> convertMoodScore(Map<MoodCategory, Double> scores) {
        return scores.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().name(),
                        entry -> String.format("%.2f...", entry.getValue())
                ));
    }

    //장르 문서 검색용 함수
    private List<Document> searchCategoryRules(List<Category> topCategory) {
        String query = topCategory.stream()
                .map(category -> category.name())
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
    private List<Document> searchPerformances(List<Category> topCategory) {
        return vectorStore.similaritySearch(
                        SearchRequest.builder()
                                .query("공연")
                                .topK(50)
                                .build()
                ).stream()
                .filter(d -> "PERFORMANCE".equals(d.getMetadata().get("type")))
                .toList();
    }


//    public CompletableFuture<ResultResponse> getRecommendations(ResultRequest request) {
//        log.info("===== 1. getRecommendations 시작 =====");
//        Map<Category, Double> categoryScores = CategoryScores(request);
//        log.info("1-1. CategoryScores 계산 완료: {}", categoryScores);
//        Map<MoodCategory, Double> moodScores = MoodScores(request);
//        log.info("1-2. MoodScores 계산 완료: {}", moodScores);
//        List<Document> performanceDocs = searchPerformances(TopCategory(request));
//          //document -> performance 변환
//        List<Performance> performancesVector = performanceDocs.stream()
//                .map(document -> Performance(document))
//                .filter(Objects::nonNull)
//                .toList();
//        //스트림 + 병렬화 처리
//        return asyncPerformances(performancesVector)
//                .thenCompose(embeddingMap -> CompletableFuture.supplyAsync(() -> {
//                    List<TypeShowScore> finalShows = FinalShowRanking(performancesVector, performancesVector, categoryScores, moodScores);
//                    List<TypeShowScore> topShows = finalShows.stream().limit(3).toList();
//
//                    String prompt = Prompt(request, categoryScores, moodScores, topShows);
//                    String content = ask(prompt);
//                    AiResponse aiResponse = parse(content);
//
//                    return toResultResponse(request, aiResponse, categoryScores, moodScores, topShows);
//                })).exceptionally(ex -> {
//                    log.error("추천 실패 fallback 적용", ex);
//                    return fallbackRecommendation(request);
//                });
//    }
//
//    private Performance Performance(Document doc) {
//        try {
//            Category category = Category.valueOf(doc.getMetadata().get("category").toString());
//            ShowCategory showCategory = category.findShowType(doc.getMetadata().get("genre").toString());
//
//            Set<MoodCategory> moods = Optional.ofNullable(doc.getMetadata().get("moods"))
//                    .map(list -> ((List<String>) list).stream()
//                            .map(m -> {
//                                try { return MoodCategory.valueOf(m); }
//                                catch (Exception ignored) { return null; }
//                            })
//                            .filter(Objects::nonNull)
//                            .collect(Collectors.toSet()))
//                    .filter(s -> !s.isEmpty())
//                    .orElseGet(() -> {
//                        if (showCategory != null && !showCategory.moods().isEmpty()) {
//                            List<MoodCategory> allowed = new ArrayList<>(showCategory.moods());
//                            return Set.of(allowed.get(new Random().nextInt(allowed.size())));
//                        }
//                        return Set.of();
//                    });
//
//            return new Performance(
//                    Integer.parseInt(doc.getMetadata().get("sq").toString()),
//                    category,
//                    showCategory,
//                    doc.getMetadata().getOrDefault("name", "").toString(),
//                    doc.getMetadata().getOrDefault("hall", "").toString(),
//                    doc.getMetadata().getOrDefault("startDate", "").toString(),
//                    doc.getMetadata().getOrDefault("endDate", "").toString(),
//                    moods
//            );
//        } catch (Exception e) {
//            log.error("Performance 변환 실패: doc={}", doc, e);
//            return null;
//        }
//    }
    //추천 받는 명령어 (프롬프트에 명령한 규칙 수행)
    public ResultResponse getRecommendations(ResultRequest request) {


        try {
            log.info("===== 1. getRecommendations 시작 =====");
            //선호하는 공연 선정 계산 ( aiResponse -> categoryScore )
            Map<Category, Double> categoryScores = CategoryScores(request);
            log.info("1-1. CategoryScores 계산 완료: {}", categoryScores);
            //선호하는 공연의 장르 선정 ( aiResponse -> moodScore )
            Map<MoodCategory, Double> moodScores = MoodScores(request);
            log.info("1-2. MoodScores 계산 완료: {}", moodScores);
            //사용자의 상위 장르 데이터 ( aiResponse -> topCategory )
            List<Category> topCategory = TopCategory(request);
            log.info("1-3. TopCategory 계산 완료: {}", topCategory);
            //사용자의 상위 분위기 데이터 ( aiResponse -> topMood )
            List<MoodCategory> topMoods = TopMoods(request);
            log.info("1-4. TopMoods 계산 완료: {}", topMoods);
            List<Performance> topCategoryPerformances = performances.stream()
                    .filter(p -> topCategory.contains(p.category()))
                    .toList();
            log.info("1-5. TopCategoryPerformances 필터링 완료, size={}", topCategoryPerformances.size());

            List<String> topCategoryShowNames = topCategoryPerformances.stream()
                    .map(performance -> performance.name())
                    .toList();
            log.info("1-6. TopCategoryShowNames 생성 완료: {}", topCategoryShowNames);

            //공연 장르 문서 검색
            List<Document> categoryDocs = searchCategoryRules(topCategory);
            log.info("1-7. CategoryDocs 검색 완료, size={}", categoryDocs.size());

            // CATEGORY 관련 문서 사용자 맞춤 규칙 검색
            Map<Category, Document> categoryRule =
                    categoryDocs.stream()
                            .collect(Collectors.toMap(
                                    document -> Category.valueOf(document.getMetadata().get("category").toString()),
                                    document -> document,
                                    (a, b) -> a
                            ));

            //공연 문서 검색
            List<Document> performanceDocs = searchPerformances(topCategory);
            log.info("1-8. PerformanceDocs 검색 완료, size={}", performanceDocs.size());

            // Document -> Performance 객체 변환
            List<Performance> performancesVector = performanceDocs.stream()
                    .map(doc -> {
                        try{
                        Category category = Category.valueOf(
                                doc.getMetadata().get("category").toString()
                        );

                        ShowCategory showCategory =
                                category.findShowType(
                                        doc.getMetadata().get("genre").toString()
                                );
                            // moods 처리: null/빈 리스트일 경우 showCategory에서 랜덤 선택
                            Set<MoodCategory> moods = Optional.ofNullable(doc.getMetadata().get("moods"))
                                    .map(list -> ((List<String>) list).stream()
                                            .map(m -> {
                                                try {
                                                    return MoodCategory.valueOf(m);
                                                } catch (Exception e) {
                                                    return null;
                                                }
                                            })
                                            .filter(Objects::nonNull)
                                            .collect(Collectors.toSet())
                                    )
                                    .orElseGet(() -> {
                                        if (showCategory != null && !showCategory.moods().isEmpty()) {
                                            List<MoodCategory> randomPick = new ArrayList<>(showCategory.moods());
                                            return Set.of(randomPick.get(new Random().nextInt(randomPick.size())));
                                        }
                                        return Set.of();
                                    });

                        return new Performance(
                                Integer.parseInt(doc.getMetadata().get("sq").toString()),
                                category,
                                showCategory,
                                doc.getMetadata().getOrDefault("name", "").toString(),
                                doc.getMetadata().getOrDefault("hall", "").toString(),
                                doc.getMetadata().getOrDefault("startDate", "").toString(),
                                doc.getMetadata().getOrDefault("endDate", "").toString(),
                                moods
                        );} catch (Exception e){
                            log.error("Performance 변환 실패: doc={}", doc, e);
                            return null;
                        }
                    })
                    .filter(object -> Objects.nonNull(object))
                    .toList();

            //장르 점수 형식대로 출력 반환 ( ResultResponse -> categoryScore )
            Map<String, String> CategoryScore =
                    convertCategoryScore(categoryScores);
            log.info("1-9. categoryscore 생성 완료");
            //분위기 점수 형식대로 출력 반환 ( ResultResponse -> moodScore )
            Map<String, String> MoodScore =
                    convertMoodScore(moodScores);
            log.info("1-10. moodscore 생성 완료");




            //공연 총합 순위 매김
            List<TypeShowScore> finalShows = FinalShowRanking(
                    topCategoryPerformances,
                    performancesVector,
                    categoryScores,
                    moodScores
            ).stream()
                    .map(typeScore -> {
                        Category category = typeScore.category();
                        ShowCategory showCategory = typeScore.showType();

                        // 룰 문서 기반 가중치 적용
                        Document ruleDoc = categoryRule.get(category);
                        double ruleBoost = 0.0;
                        if (ruleDoc != null && ruleDoc.getText().contains(showCategory.typeName())) {
                            ruleBoost = 2.0;
                        }

                        double newScore = typeScore.TypeScore() + ruleBoost;
                        return new TypeShowScore(category, showCategory, newScore, typeScore.performance());
                    })
                    .sorted(Comparator.comparing((TypeShowScore typeShowScore) -> typeShowScore.TypeScore()).reversed())
                    .toList();
            log.info(" finalShows 생성 완료");
            // 상위 3개만 사용
            List<TypeShowScore> topScoreShows = finalShows.stream()
                    .limit(3)
                    .toList();
            log.info("topScoreShows");

            // PERFORMANCE 문서 기반 ( Recommendations 통합 처리 )
            List<Recommendations> performanceRecommendations =  topScoreShows.stream()
                    .map(TypeScore -> {
                        Performance performance = TypeScore.performance();
                        ShowCategory showCategory = TypeScore.showType();
                        Category category = TypeScore.category();


                        //룰 기반 가중치
                        String reason = "사용자 점수 기반 추천";
                        Document ruleDoc = categoryRule.get(category);
                        if (ruleDoc != null && ruleDoc.getText().contains(showCategory.typeName())) {
                            reason += " + 카테고리 룰 기반 추천";
                        }

                        Set<MoodCategory> mood =
                                showCategory.pickMood(showCategory, moodScores);

                        return new Recommendations(
                                category,
                                reason,
                                mood,
                                showCategory,
                                performance.name(),
                                performance.hall(),
                                performance.startDate(),
                                performance.endDate()
                        );
                    })
                    .toList();
            log.info("performanceRecommendations");

            // 공연 이름 리스트 ( Recommendations -> showCategory )
            List<String> topFinalShows = performanceRecommendations.stream()
                    .map(recommendations -> recommendations.showCategory().typeName())
                    .toList();
            log.info("topFinalShows");


            // 비슷한 공연 추천 ( ResultResponse -> similarShows )
            List<String> similarShows = SimilarShows(topScoreShows, performancesVector);

            //공연 장르 관련 리스트 ( ResultResponse -> similarTopShows )
            List<String> similarTopShows = topScoreShows.stream()
                    .map(score -> score.showType().typeName())
                    .distinct()
                    .toList();

            String prompt = """
                    공연 장르 추천해주는 서비스입니다.
                    아래 사용자 정보와 공연 목록을 기반으로
                    [규칙]
                    1. 반드시 JSON만 출력한다.
                    2. category 값은 다음 중 하나만 사용한다:
                       MUSICAL, CONCERT, PLAY, CLASSIC
                    3. mood 값은 다음중 하나만 사용한다:
                        FANTASY, HORROR, ROMANCE, COMEDY, ACTION
                    4. summary, explanations, recommendations 외 다른 텍스트 금지
                    JSON은 ``` 같은 코드블록으로 감싸지 말고
                    순수 JSON 텍스트만 출력하라.
                    5. recommendations는 사용자의 점수 기반 우선순위를 반영
                    - categoryScore: 각 장르 점수
                    - moodScore: 각 카테고리 점수
                    6. 절대로 문장이나 설명을 JSON 밖에 출력하지 마세요.
                    7. ``` 같은 코드블록 제거 후 순수 JSON만 출력
                    

                    [Category 규칙]
                    - category 값은 반드시 아래 중 하나만 사용:
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
                    
                    [사용자 정보]
                    - 관심 카테고리: %s
                    - 최근 구매 카테고리: %s
                    - 선호 무드: %s
                    
                    [카테고리 점수]
                    %s
                    
                    [무드 점수]
                    %s
                    
                    [이미 계산된 추천 후보]
                    %s
                    
                    [출력 형식]
                    {
                    "summary": "추천 요약 문장",
                    "explanations": ["설명 문장1", "설명 문장2"],
                    "recommendations": [
                    {
                    "category": "장르", "장르" ,
                    "reason": "추천 이유",
                    "mood": "분위기", "분위기"
                    "topFinalShow" : 해당 카테고리에 속한 공연 종류
                    "categoryScore": { "장르": 점수, "장르": 점수, ... },
                    "moodScore": { "mood": 1.0, "mood": 2.0, ... },
                    "topCategory": ["category", "category"],
                    "topMood": ["mood", "mood]
                    "similarShows": 공연 추천
                    "similarTopShow" : 공연 종류 리스트
                            }
                        ]
                    }
                    """.formatted(
                            request.attention(), request.category(),
                            topCategory, topMoods,
                            CategoryScore, MoodScore,
                            topFinalShows, similarTopShows,
                            similarShows
            );

            //ai 호출 -> 프롬프트 입력
            String content = ask(prompt);
            log.info("AI RAW RESPONSE = {}", content);

            //ai에게 받는 응답을 문자열 파싱
            AiResponse aiResponse = parse(content);


            //응답 반환
            return new ResultResponse(
                    "사용자 맞춤 공연 추천입니다.",
                    List.of("상위 카테고리 및 무드 기반 추천"),
                    performanceRecommendations,
                    CategoryScore,
                    MoodScore,
                    topCategory.stream().map(category -> category.name()).toList(),
                    topMoods.stream().map(moodCategory -> moodCategory.name()).toList(),
                    topFinalShows,
                    similarShows,
                    similarTopShows,
                    topCategoryShowNames
            );

        } catch (Exception e) {
            return fallbackRecommendation(request);
        }

    }

    //비슷한 장르 찾기
    private List<String> findSimilarShows(List<Recommendations> fallbackRecommendations) {
        Set<String> similarShows = new HashSet<>();

        for (Recommendations rec : fallbackRecommendations) {
            Category category = rec.category();
            ShowCategory topShowType = rec.showCategory();
            Set<MoodCategory> topMoods = topShowType.moods();

            List<? extends ShowCategory> candidates = category.getTypes().stream()
                    .filter(showCategory -> !showCategory.equals(topShowType))
                    .toList();

            Map<ShowCategory, Integer> score = new HashMap<>();
            for (ShowCategory candidate : candidates) {
                long commonMoodCount = candidate.moods().stream()
                        .filter(MoodCategory -> topMoods.contains(MoodCategory)).count();
                score.put(candidate, (int) commonMoodCount);
            }

            score.entrySet().stream()
                    .sorted(Map.Entry.<ShowCategory, Integer>comparingByValue().reversed())
                    .map(entry -> entry.getKey().typeName()).limit(2)
                    .forEach(e -> similarShows.add(e));
        }

        return new ArrayList<>(similarShows);
    }

    /**
     * ai 호출 실패용 기본 결과값 출력
     * @param request
     * @return
     */
    //ai 요청 실패시 기본결과 값으로 호출
    public ResultResponse fallbackRecommendation(
            ResultRequest request
    ) {
        List<Recommendations> fallbackRecommendations = List.of(
                new Recommendations(
                        Category.MUSICAL,
                        "스토리와 음악이 뛰어난 뮤지컬",
                        Set.of(Category.MUSICAL.getTypes().iterator().next().moods().iterator().next()),
                        Category.MUSICAL.getTypes().iterator().next(), "", "","",""
                ),
                new Recommendations(Category.CONCERT, "라이브 공연 선호", Set.of(Category.CONCERT.getTypes().iterator().next().moods().iterator().next()), Category.CONCERT.getTypes().iterator().next(),"", "","",""),
                new Recommendations(Category.PLAY, "대화 중심 작품 선호", Set.of(Category.PLAY.getTypes().iterator().next().moods().iterator().next()), Category.PLAY.getTypes().iterator().next(),"", "","",""),
                new Recommendations(Category.CLASSIC, "차분한 분위기, 클래식 선호", Set.of(Category.CLASSIC.getTypes().iterator().next().moods().iterator().next()), Category.CLASSIC.getTypes().iterator().next(),"", "","","")
        );

        Map<String, String> categoryScore = convertCategoryScore(CategoryScores(request));
        Map<String, String> moodScore = convertMoodScore(MoodScores(request));
        List<String> topCategory = TopCategory(request).stream().map(category -> category.name()).toList();
        List<String> topMood = TopMoods(request).stream().map(moodCategory -> moodCategory.name()).toList();
        List<String> topFinalShows = fallbackRecommendations.stream().map(recommendations -> recommendations.showCategory().typeName()).toList();
        List<String> similarShows = findSimilarShows(fallbackRecommendations);
        List<String> similarTopShows = fallbackRecommendations.stream()
                .map(recommendations -> recommendations.showCategory().typeName())
                .distinct().limit(3).toList();
        List<Performance> performances = performanceRepository.findAll();
        List<String> topCategoryShowNames = performances.stream()
                .filter(performance -> topCategory.contains(performance.category().name()))
                .map(performance -> performance.name()).limit(10).toList();

        return new ResultResponse(
                "기본 추천 결과입니다.",
                List.of("AI 응답 실패로 기본 추천을 제공합니다."),
                fallbackRecommendations,
                categoryScore,
                moodScore,
                topCategory,
                topMood,
                topFinalShows,
                similarShows,
                similarTopShows,
                topCategoryShowNames
        );
    }
}
