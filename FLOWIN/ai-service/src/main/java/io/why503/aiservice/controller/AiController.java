package io.why503.aiservice.controller;


import io.why503.aiservice.model.vo.ResultRequest;
import io.why503.aiservice.model.vo.ResultResponse;
import io.why503.aiservice.service.AiService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/recommend")
public class AiController {
    private final AiService aiService;

    //추천 결과값 반환
    @PostMapping
    public ResultResponse getRecommendations(
            @RequestBody ResultRequest r
    ) {
        return aiService.getRecommendations(r);
    }


//        private final MatrixFactorizationService mfService;
//        private final Map<Integer, List<Integer>> categoryToItems;
//        private final Map<Integer, double[]> categoryEmbeddings;
//
//        public RecommendationController(MatrixFactorizationService mfService,
//                                        Map<Integer, List<Integer>> categoryToItems,
//                                        Map<Integer, double[]> categoryEmbeddings) {
//            this.mfService = mfService;
//            this.categoryToItems = categoryToItems;
//            this.categoryEmbeddings = categoryEmbeddings;
//        }
//
//        @GetMapping("/performance/{userId}")
//        public List<Integer> recommendPerformances(@PathVariable int userId, @RequestParam(defaultValue="5") int topN) {
//            Map<Integer, Double> scores = mfService.computeUserItemScores(userId);
//            return topN(scores, topN);
//        }
//
//        @GetMapping("/category/{userId}")
//        public List<Integer> recommendCategories(@PathVariable int userId, @RequestParam(defaultValue="3") int topN) {
//            // 임베딩 기반 카테고리 점수
//            double[] userEmbedding = mfService.getUserEmbedding(userId);
//            Map<Integer, Double> catScores = computeCategoryScoresByEmbedding(userEmbedding, categoryEmbeddings);
//            return topN(catScores, topN);
//        }

}