package io.why503.aiservice.domain.ai.repository.impl;

import io.why503.aiservice.domain.ai.model.embedding.Performance;
import io.why503.aiservice.global.client.dto.response.PerformanceResponse;

import java.util.List;


public interface PerformanceRepository {
    //공연 정보 벡터 저장 jpa 연결
    void saveAll(List<Performance> performances);
    //공연 데이터
    List<PerformanceResponse> findAllResponses();
}
