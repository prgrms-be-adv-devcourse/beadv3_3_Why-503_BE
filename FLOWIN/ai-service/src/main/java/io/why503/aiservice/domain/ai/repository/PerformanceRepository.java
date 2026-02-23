package io.why503.aiservice.domain.ai.repository;

import io.why503.aiservice.domain.ai.model.embedding.Performance;
import io.why503.aiservice.global.client.dto.response.PerformanceResponse;
import io.why503.aiservice.global.client.dto.response.BookingResponse;

import java.util.List;


public interface PerformanceRepository {
    List<Performance> findAll();
    //공연 정보 벡터 저장 jpa 연결
    void saveAll(List<Performance> performances);
    List<BookingResponse> findStatus();
    //공연 데이터
    List<PerformanceResponse> findAllResponses();
}
