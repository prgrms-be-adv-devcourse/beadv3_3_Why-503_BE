package io.why503.aiservice.domain.ai.service;

import io.why503.aiservice.domain.ai.model.embedding.ShowCategory;
import io.why503.aiservice.domain.ai.model.vo.ResultRequest;
import org.springframework.ai.document.Document;

import java.util.List;

public interface VectorSearch {
    float[] embed(ResultRequest request, Long userSq);
    List<Document> searchCategoryRules(List<ShowCategory> topShowCategory);
    List<Document> searchPerformances(List<ShowCategory> topShowCategory);
}
