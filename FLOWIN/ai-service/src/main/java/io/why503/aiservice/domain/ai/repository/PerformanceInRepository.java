package io.why503.aiservice.domain.ai.repository;

import io.why503.aiservice.domain.ai.model.embedding.Performance;
import io.why503.aiservice.global.client.dto.response.PerformanceResponse;
import io.why503.aiservice.global.client.dto.response.BookingResponse;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PerformanceInRepository implements PerformanceRepository {

    private final List<Performance> store = new ArrayList<>();

    @Override
    public List<Performance> findAll() {
        return new ArrayList<>(store);
    }

    @Override
    public void saveAll(List<Performance> performances) {
        store.clear();
        store.addAll(performances);
    }

    @Override
    public List<BookingResponse> findStatus() {
        return List.of();
    }

    @Override
    public List<PerformanceResponse> findAllResponses() {
        return List.of();
    }
}