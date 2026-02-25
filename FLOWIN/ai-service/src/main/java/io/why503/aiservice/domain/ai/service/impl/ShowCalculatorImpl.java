package io.why503.aiservice.domain.ai.service.impl;

import io.why503.aiservice.global.client.dto.response.Performance;
import io.why503.aiservice.domain.ai.model.embedding.ShowCategory;
import io.why503.aiservice.domain.ai.model.embedding.genre.ShowGenre;
import io.why503.aiservice.domain.ai.model.vo.ResultRequest;
import io.why503.aiservice.domain.ai.model.vo.TypeShowScore;
import io.why503.aiservice.domain.ai.service.ShowCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class ShowCalculatorImpl implements ShowCalculator {

    private final VectorStore vectorStore;
    private final EmbeddingModel embeddingModel;

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
    public Map<ShowCategory, Double> categoryScores(ResultRequest request, float[] userVector) {
        Map<ShowCategory, Double> scoreMap = new EnumMap<>(ShowCategory.class);

        //구매 횟수 (가중치 적용하기 위해 장르 중복 발생 시 로그 처리)
        Map<String, Long> categoryCount = new HashMap<>();
        categoryCount.put(request.showCategory(), 1L);


        //로그 + 가중치 (중복 발생 시 로그 적용한 다음에 작은 수로 비율 적용)
        categoryCount.forEach((showCategory, count) -> {
            double score = Math.log1p(count) * 3.0;
            scoreMap.merge(ShowCategory.fromString(showCategory), score, (a, b) -> Double.sum(a, b));
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
    public Map<String, Double> genreScores(ResultRequest request, Map<ShowCategory, Double> categoryScores) {

        Map<String, Double> scoreMap = new HashMap<>();

        //사용자가 선택한 장르 직접 가중치
        if (request.genre() != null) {
            scoreMap.put(request.genre(), 1.0);
        }

        for (ShowCategory showCategory : ShowCategory.values()) {
            double categoryScore = categoryScores.getOrDefault(showCategory, 0.0);

            for (ShowGenre show : showCategory.getTypes()) {
                scoreMap.merge(show.getName(), categoryScore * 0.5, (a, b) -> Double.sum(a, b));
            }
        }

        double percent100 = 100.0;
        double total = scoreMap.values().stream()
                .mapToDouble(Double -> Double.doubleValue())
                .sum();

        Map<String, Double> percentMap = new HashMap<>();

        for (Map.Entry<String, Double> entry : scoreMap.entrySet()) {
            double percent = total == 0
                    ? 0.0
                    : (entry.getValue() / total) * percent100;
            percentMap.put(entry.getKey(), percent);
        }

        return percentMap;
    }

    //최종 점수 = 카테고리 점수 50% + 분위기 점수 합 50% ( 최종 추천에 대한 점수 계산 )
    public double finalScore(
            Performance performance,
            Map<ShowCategory, Double> categoryScores,
            Map<String, Double> genreScores
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
    public List<TypeShowScore> finalShowRanking(
            List<Performance> performances,
            Map<ShowCategory, Double> categoryScores,
            Map<String, Double> genreScores
    ) {
        List<TypeShowScore> results = new ArrayList<>();

        //공연의 점수를 지정된 것들을 포장
        for (Performance performance : performances) {
            double score = finalScore(performance, categoryScores,genreScores);
            results.add(new TypeShowScore(performance.category(), performance.genre(), score, performance));

        }
        results.sort(Comparator.comparing((TypeShowScore typeShowScore) -> typeShowScore.typeScore()).reversed());

        return results;
    }
}
