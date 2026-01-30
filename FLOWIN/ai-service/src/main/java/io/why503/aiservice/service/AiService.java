package io.why503.aiservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.why503.aiservice.model.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AiService {

    private final ChatClient chatClient;
    private final ObjectMapper mapper = new ObjectMapper();
    private final VectorStore vectorStore;
    private final EmbeddingModel embeddingModel;



    //ai 프롬프트 보냄(AI서버) -> 문자열 응답식으로 받음
    public String ask(String prompt) {
        return chatClient.prompt(prompt)
                .call()
                .content();
    }

    //포스트맨에서 json 형식으로 받기 위한 파싱
    public AiResponse parse(String content) {
        try {
            String cleaned = cleanJson(content);
            return mapper.readValue(cleaned, AiResponse.class);
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
                    
                    [사용자 선호]
                    - 관심 카테고리: %s
                    - 최근 구매: %s
                    
                    
                    [출력 형식]
                    {
                        "summary": "추천 요약 문장",
                        "explanations": ["설명 문장1",
                        "설명 문장2"
                        ],
                        "recommendations": [
                        {
                            "category": "PLAY | MUSICAL | CONCERT | CLASSIC",
                            "reason": "추천 이유"
                            }
                           ]
                        }
                    """.formatted(r.category(), r.attention());

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

    //ai 요청 실패시 기본결과 값으로 호출
    public ResultResponse fallbackRecommendation(
            ResultRequest r
    ) {
            return new ResultResponse(
                    "기본 추천 결과입니다.",
                    List.of("AI 응답 실패로 기본 추천을 제공합니다."),
                    List.of(
                    new Recommendations(Category.MUSICAL, "스토리와 음악이 뛰어난 뮤지컬"),
                    new Recommendations(Category.CONCERT, "라이브 공연 선호"),
                    new Recommendations(Category.PLAY, "대화 중심 작품 선호"),
                    new Recommendations(Category.CLASSIC, "차분한 분위기, 클래식 선호")
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
                    .map(category ->  new Recommendations(category, ar.reason())
            );
    }



}
