package io.why503.aiservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.why503.aiservice.model.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

    private final ChatClient chatClient;
    private final ObjectMapper mapper;
    private final VectorStore vectorStore;
    private final EmbeddingModel embeddingModel;
    private final Random random = new Random();



    //사용자가 이 문자열 입력에 의해 임베딩 모델 학습 (텍스트 -> 숫자) / float []
    public float[] embed(ResultRequest r) {

        String text = """
                      사용자의 공연 선호 정보입니다.
                      최근 구매한 장르: %s
                      관심 있는 장르: %s
                      선호 무드: %s
                      """.formatted(
                            r.category(),
                            r.attention(),
                            r.mood() != null ? r.mood() : List.of()
                      );
        return embeddingModel.embed(text);
    }



    //두 백터 간 코사인 유사도 계산
    public double cosineSimilarity(float[] vectorA, float[] vectorB) {

        //같은 벡터 비교
        if ( vectorA.length != vectorB.length ) {
            throw new IllegalArgumentException("Vectors must be of equal length");
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
    private Map<Category, Double> calculateCategoryScores(ResultRequest r) {
        Map<Category, Double> scoreMap = new EnumMap<>(Category.class);

        // 기본 점수: 최근 구매 3, 관심 1 가중치 부여
        for (Category c : r.category()) scoreMap.merge(c, 3.0, (a, b) -> Double.sum(a, b));
        for (Category c : r.attention()) scoreMap.merge(c, 1.0, (a, b) -> Double.sum(a, b));

        // 사용자 행동을 임베딩
        float[] userVector = embed(r);

        // 각 장르별로 유사도 계산
        for (Category c : Category.values()) {
            List<Document> docs = vectorStore.similaritySearch(
                    SearchRequest.builder().query(c.name()).topK(3).build()
            );

            double maxSim = docs.stream()
                    .map(doc -> cosineSimilarity(userVector, embeddingModel.embed(doc.getText().toString())))
                    .max((d1, d2) -> Double.compare(d1, d2))
                    .orElse(0.0);
            scoreMap.merge(c, maxSim * 5, (a, b) -> Double.sum(a, b));
            }

            return scoreMap;
    }


    //카테고리 점수 계산
    private Map<MoodCategory, Double> calculateMoodScores(ResultRequest r) {
        if (r.mood() == null || r.mood().isEmpty()) return Map.of();
        Map<MoodCategory, Double> scoreMap = new EnumMap<>(MoodCategory.class);

        // 기본 점수
        for (MoodCategory m : r.mood()) scoreMap.merge(m, 1.0, (a, b) -> Double.sum(a, b));

        // 임베딩 기반 유사도
        float[] moodVector = embeddingModel.embed(
                String.join(" ", r.mood().stream().map(moodCategory -> moodCategory.name()).toList())
        );

        for (MoodCategory m : MoodCategory.values()) {
            List<Document> docs = vectorStore.similaritySearch(
                    SearchRequest.builder().query(m.name()).topK(3).build()
            );

            double maxSim = docs.stream()
                    .map(doc -> cosineSimilarity(moodVector, embeddingModel.embed(doc.getText().toString())))
                    .max((d1, d2) -> Double.compare(d1, d2))
                    .orElse(0.0);
            scoreMap.merge(m, maxSim * 5, (a, b) -> Double.sum(a, b));

        }
        return scoreMap;
    }

    // 사용자의 행동 기반 상위 장르 결정
    private List<Category> decideTopCategoryByEmbedding(ResultRequest r) {
        Map<Category, Double> scores = calculateCategoryScores(r);
        return scores.entrySet().stream()
                .sorted(Map.Entry.<Category, Double>comparingByValue().reversed())
                .map(categoryDoubleEntry -> categoryDoubleEntry.getKey())
                .limit(2)
                .toList();
    }
    private List<MoodCategory> decideTopMoods(ResultRequest r) {
        if (r.mood() == null || r.mood().isEmpty()) return List.of();

        Map<MoodCategory, Double> scores = calculateMoodScores(r);
        return scores.entrySet().stream()
                .sorted(Map.Entry.<MoodCategory, Double>comparingByValue().reversed())
                .map(moodCategoryDoubleEntry -> moodCategoryDoubleEntry.getKey())
                .limit(2)
                .toList();
    }


    // 상위 장르 문서 검색
    private List<Document> searchCategoryDocs(List<Category> topCategory) {
        String query = topCategory.stream().map(category -> category.name()).collect(Collectors.joining(" "));
        SearchRequest searchRequest = SearchRequest.builder()
                .query(query)
                .topK(topCategory.size())
                .build();
        return vectorStore.similaritySearch(searchRequest);
    }



    //ai 프롬프트 보냄(AI서버) -> 문자열 응답식으로 받음
    public String ask(String prompt) {
        return chatClient.prompt(prompt)
                .call()
                .content();
    }


    //포스트맨에서 json 형식으로 받기 위한 파싱
    public AiResponse parse(String content) {
        try {
            return mapper.readValue(cleanJson(content), AiResponse.class);
        } catch (Exception e) {
            log.error("AI 응답 파싱 실패. content={}", content, e);
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
    private Recommendations toDomain(AiRecommendation ar) {
        Category category = Category.fromString(ar.category()).orElse(Category.PLAY);
        MoodCategory mood = MoodCategory.fromString(ar.mood()).orElse(randomMood());
        return new Recommendations(category, ar.reason(), mood);
    }

    //기본결과에 랜덤 카테고리 적용
    private MoodCategory randomMood() {
        MoodCategory[] moods = MoodCategory.values();
        return moods[random.nextInt(moods.length)];
    }

    private Map<String, Double> convertCategoryScore(Map<Category, Double> scores) {
        return scores.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().name(), categoryDoubleEntry -> categoryDoubleEntry.getValue()));
    }

    private Map<String, Double> convertMoodScore(Map<MoodCategory, Double> scores) {
        return scores.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().name(), moodCategoryDoubleEntry -> moodCategoryDoubleEntry.getValue()));
    }


    //추천 받는 명령어 (프롬프트에 명령한 규칙 수행)
    public ResultResponse getRecommendations(ResultRequest r) {

        try {

            Map<Category, Double> categoryScores = calculateCategoryScores(r);
            Map<MoodCategory, Double> moodScores = calculateMoodScores(r);

            //사용자의 상위 장르 데이터
            List<Category> topCategories = decideTopCategoryByEmbedding(r);
            List<MoodCategory> topMoods = decideTopMoods(r);


            //상위 2개 뽑아 스트림 값으로 문서 검색
            List<Document> docs = searchCategoryDocs(topCategories);
            String docText = docs.stream()
                    .map(document -> document.getText().toString())
                    .collect(Collectors.joining("\n"));


            String prompt = """
                    공연 장르 추천해주는 서비스입니다.
                    아래 사용자 정보와 공연 목록을 기반으로
                    [규칙]
                    1. 반드시 JSON만 출력한다.
                    2. category 값은 다음 중 하나만 사용한다:
                       MUSICAL, CONCERT, PLAY, CLASSIC (소문자 사용 금지)
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
                    
                    
                    [사용자 정보]
                    -관심 카테고리: %s
                    -최근 구매: %s
                    [장르 점수]: %s
                    -상위 장르: %s
                    [무드 점수]: %s
                    -상위 무드: %s
                    [장르 문서]: %s
                    [출력 형식]
                    {
                    "summary": "추천 요약 문장",
                    "explanations": ["설명 문장1", "설명 문장2"],
                    "recommendations": [
                    {
                    "category": "PLAY | MUSICAL | CONCERT | CLASSIC",
                    "reason": "추천 이유",
                    "mood": "FANTASY | HORROR | ROMANCE | COMEDY | ACTION"
                    "categoryScore": { "MUSICAL": 3.0, "CONCERT": 2.0, ... },
                    "moodScore": { "COMEDY": 1.0, "ROMANCE": 2.0, ... },
                    "topCategory": ["MUSICAL", "CONCERT"],
                    "topMood": ["COMEDY", "ROMANCE"]
                            }
                        ]
                    }
                    """.formatted(r.attention(), r.category(),
                    topCategories, topMoods,
                    convertCategoryScore(categoryScores), convertMoodScore(moodScores),
                    docText);

            //ai 호출 -> 프롬프트 입력
            String content = ask(prompt);
            log.info("AI RAW RESPONSE = {}", content);

            //ai에게 받는 응답을 문자열 파싱
            AiResponse aiResponse = parse(content);

            List<AiRecommendation> recommendations = Optional.ofNullable(aiResponse.recommendations())
                    .orElse(List.of()).stream()
                    .map(ar -> new AiRecommendation(
                            ar.category(),
                            ar.reason(),
                            ar.mood() != null ? ar.mood() : randomMood().name(),
                            ar.explanations()
                    ))
                    .toList();

            AiResponse fixedResponse = new AiResponse(
                    Optional.ofNullable(aiResponse.summary()).orElse(""),
                    Optional.ofNullable(aiResponse.explanations()).orElse(List.of()),
                    recommendations,
                    Optional.ofNullable(aiResponse.categoryScore()).orElse(convertCategoryScore(categoryScores)),
                    Optional.ofNullable(aiResponse.moodScore()).orElse(convertMoodScore(moodScores)),
                    Optional.ofNullable(aiResponse.topCategory()).orElse(topCategories.stream().map(category -> category.name()).toList()),
                    Optional.ofNullable(aiResponse.topMood()).orElse(topMoods.stream().map(moodCategory -> moodCategory.name()).toList())
            );

            //AiRecommendation -> 스트림, Recommendations -> 스트림
            List<Recommendations> finalRecommendations =
                    fixedResponse.recommendations().stream()
                            .map(ar1 -> toDomain(ar1))
                            .toList();

            return new ResultResponse(
                    fixedResponse.summary(),
                    fixedResponse.explanations(),
                    finalRecommendations,
                    fixedResponse.categoryScore(),
                    fixedResponse.moodScore(),
                    fixedResponse.topCategory(),
                    fixedResponse.topMood()
            );
        } catch (Exception e) {
            return fallbackRecommendation(r);
        }
    }

    //ai 요청 실패시 기본결과 값으로 호출
    public ResultResponse fallbackRecommendation(
            ResultRequest r
    ) {
        List<Recommendations> fallbackRecommendations = List.of(
                new Recommendations(Category.MUSICAL, "스토리와 음악이 뛰어난 뮤지컬", randomMood()),
                new Recommendations(Category.CONCERT, "라이브 공연 선호", randomMood()),
                new Recommendations(Category.PLAY, "대화 중심 작품 선호", randomMood()),
                new Recommendations(Category.CLASSIC, "차분한 분위기, 클래식 선호", randomMood())
        );

        Map<String, Double> categoryScore = convertCategoryScore(calculateCategoryScores(r));
        Map<String, Double> moodScore = convertMoodScore(calculateMoodScores(r));

        List<String> topCategory = decideTopCategoryByEmbedding(r).stream().map(category -> category.name()).toList();
        List<String> topMood = decideTopMoods(r).stream().map(moodCategory -> moodCategory.name()).toList();

        return new ResultResponse(
                "기본 추천 결과입니다.",
                List.of("AI 응답 실패로 기본 추천을 제공합니다."),
                fallbackRecommendations,
                categoryScore,
                moodScore,
                topCategory,
                topMood
        );

    }

}
