package io.why503.aiservice.domain.ai.service.impl;

import io.why503.aiservice.domain.ai.service.VectorStoreSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 벡터스토어 검색을 위한 클래스
 */
@RequiredArgsConstructor
@Component
public class VectorStoreSearchImpl implements VectorStoreSearch {

    private final VectorStore vectorStore;

    //공연 문서 검색용 함수
    public List<Document> searchPerformances(String query) {

        return vectorStore.similaritySearch(
                        SearchRequest.builder()
                                .query(query)
                                .topK(5)
                                .build()
                );
    }
}
