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
    private final ObjectMapper mapper = new ObjectMapper();
    private final VectorStore vectorStore;
    private final EmbeddingModel embeddingModel;


    //사용자가 이 문자열 입력에 의해 임베딩 모델 학습 (텍스트 -> 숫자)
    public float[] embed(ResultRequest r) {

        String text = """
    사용자의 공연 선호 정보입니다.
    최근 구매한 장르: %s
    관심 있는 장르: %s
    """.formatted(
                r.category(),
                r.attention()
        );
        return embeddingModel.embed(text);
    }

    public double getSimilarity(String content1, String content2) {
        List<float[]> vectorList = embeddingModel.embed(List.of(content1, content2));

        return cosineSimilarity(vectorList.get(0), vectorList.get(1));
    }



    //사용자의 장르 유사도
    public double cosineSimilarity(float[] vectorA, float[] vectorB) {

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


        return dotProduct / (Math.sqrt(magnitudeA) * Math.sqrt(magnitudeB));

    }

    // 사용자의 행동 기반 상위 장르 결정
    private List<Category> decideTopCategory(ResultRequest r) {
        Map<Category, Integer> scoreMap = new EnumMap<>(Category.class);

        for (Category c : r.category()) scoreMap.merge(c, 3, Integer::sum);
        for (Category c : r.attention()) scoreMap.merge(c, 1, Integer::sum);

        // 점수 로그 출력
        scoreMap.forEach((category, score) -> log.info("장르: {}, 점수: {}", category, score));

        return scoreMap.entrySet().stream()
                .sorted(Map.Entry.<Category, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .limit(2)
                .toList();
    }

    // 상위 장르 문서 검색
    private List<Document> searchCategoryDocs(List<Category> topCategory) {
        String query = topCategory.stream().map(Category::name).collect(Collectors.joining(" "));
        SearchRequest searchRequest = SearchRequest.builder()
                .query(query)
                .topK(topCategory.size()) // 장르별 문서 가져오기
                .build();
        return vectorStore.similaritySearch(searchRequest);
    }

    private List<MoodCategory> decideTopMoods(ResultRequest r) {
        if (r.mood() == null || r.mood().isEmpty()) return List.of();
        // 단순히 빈도 기반 예시
        Map<MoodCategory, Integer> scoreMap = new EnumMap<>(MoodCategory.class);
        for (MoodCategory m : r.mood()) scoreMap.merge(m, 1, Integer::sum);

        return scoreMap.entrySet().stream()
                .sorted(Map.Entry.<MoodCategory, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .limit(2)
                .toList();
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
        if (content == null) {
            return "";
        }

        String trimmed = content.trim();

        // ```json ... ``` 또는 ``` ... ``` 형태 제거
        if (trimmed.startsWith("```")) {
            // 시작 ``` 또는 ```json 제거
            trimmed = trimmed.replaceFirst("^```json", "")
                    .replaceFirst("^```", "");

            // 끝 ``` 제거
            trimmed = trimmed.replaceFirst("```$", "")
                    .trim();
        }

        return trimmed;
    }

    //추천 받는 명령어 (프롬프트에 명령한 규칙 수행)
    public ResultResponse getRecommendations(ResultRequest r) {

        try {
            //사용자의 상위 장르 데이터
            List<Category> topCategory = decideTopCategory(r);
            List<MoodCategory> topMoods = decideTopMoods(r);

            //상위 2개 뽑아 스트림 값으로 문서 검색
            List<Document> docs = searchCategoryDocs(topCategory);

            String docText = docs.stream()
                    .map(document -> document.getText().toString())
                    .collect(Collectors.joining("\n"));


            String prompt = """
                    공연 장르 추천해주는 서비스입니다.
                    아래 사용자 정보와 공연 목록을 기반으로
                    [규칙]
                    1. 반드시 JSON만 출력한다.
                    2. 설명 문장은 summary 와 explanations 필드에만 작성한다.
                    3. category 값은 다음 중 하나만 사용한다:
                       MUSICAL, CONCERT, PLAY, CLASSIC (소문자 사용 금지)
                    4. JSON 외의 어떤 텍스트도 출력하지 마라.
                    JSON은 ``` 같은 코드블록으로 감싸지 말고
                    순수 JSON 텍스트만 출력하라.
                    

                    [추천 근거 문서]
                    - 관심 카테고리: %s
                    - 최근 구매: %s
                    
                    [사용자 상위 장르]: %s
                    
                    [장르 설명 문서]: %s

                    [출력 형식]
                    {
                        "summary": "추천 요약 문장",
                        "explanations": ["설명 문장1",
                        "설명 문장2"
                        ],
                        "recommendations": [
                        {
                            "category": "PLAY | MUSICAL | CONCERT | CLASSIC",
                            "reason": "추천 이유",
                            "mood: "FANTASY| HORROR | ROMANCE | COMEDY | ACTION"
                            }
                           ]
                        }
                    """.formatted(r.attention(), r.category(), topCategory, topMoods, docText);

            //ai 호출 -> 프롬프트 입력
            String content = ask(prompt);
            log.info("AI RAW RESPONSE = {}", content);

            //ai에게 받는 응답을 문자열 파싱
            AiResponse aiResponse = parse(content);

            //AiRecommendation -> 스트림, Recommendations -> 스트림
            List<Recommendations> recommendations =
                    aiResponse.recommendations().stream()
                            .map(ar -> toDomain(ar))
                            .flatMap(recommendations1 -> recommendations1.stream())
                            .toList();

            //최종 문서 응답 반환
            return new ResultResponse(
                    aiResponse.summary(),
                    aiResponse.explanations(),
                    recommendations
            );
        } catch (Exception e) {
            return fallbackRecommendation(r);
        }

    }

    //기본결과에 랜덤 카테고리 적용
    private final Random random = new Random();

    private MoodCategory randomMood() {
        MoodCategory[] moods = MoodCategory.values();
        return moods[random.nextInt(moods.length)];
    }
    //ai 요청 실패시 기본결과 값으로 호출
    public ResultResponse fallbackRecommendation(
            ResultRequest r
    ) {


        return new ResultResponse(

        "기본 추천 결과입니다.",
                    List.of("AI 응답 실패로 기본 추천을 제공합니다."),
                    List.of(
                    new Recommendations(Category.MUSICAL, "스토리와 음악이 뛰어난 뮤지컬", randomMood()),
                    new Recommendations(Category.CONCERT, "라이브 공연 선호", randomMood()),
                    new Recommendations(Category.PLAY, "대화 중심 작품 선호", randomMood()),
                    new Recommendations(Category.CLASSIC, "차분한 분위기, 클래식 선호", randomMood())
                    )
            );
    }

    /**
     * aiResponse -> resultResponse 타입 변환
     * ai용 추천 요약 문장, 설명 문장 포함 모든 정보  -> 사용자용 장르, 추천 이유
     */
    private Optional<Recommendations> toDomain(AiRecommendation ar) {
            log.info("domain was raw");
            return Category.fromString(ar.category())
                    .map(category -> {
                        MoodCategory mood = MoodCategory.fromString(ar.mood())
                                .orElse(null);
                        return new Recommendations(category, ar.reason(), mood);
                    });
    }

}
