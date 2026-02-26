package io.why503.aiservice.domain.ai.util.mapper;

import io.why503.aiservice.domain.ai.model.dto.response.RecommendResponse;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RecommendationMapper {

    public List<RecommendResponse> DocsToRecommendList(List<Document> performanceDocs) {
        return performanceDocs.stream()
                .map(docs -> new RecommendResponse(
                        Long.valueOf(docs.getId()),
                        docs.getMetadata().get("name").toString()
                )).toList();
    }
}