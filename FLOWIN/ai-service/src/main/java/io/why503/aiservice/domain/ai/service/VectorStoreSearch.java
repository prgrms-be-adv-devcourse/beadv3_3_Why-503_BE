package io.why503.aiservice.domain.ai.service;

import org.springframework.ai.document.Document;

import java.util.List;

public interface VectorStoreSearch {
    List<Document> searchPerformances(String query);
}
