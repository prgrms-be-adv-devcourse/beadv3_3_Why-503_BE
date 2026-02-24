package io.why503.aiservice.domain.ai.repository.impl;

import io.why503.aiservice.domain.ai.model.embedding.Performance;
import io.why503.aiservice.domain.ai.repository.PerformanceRepository;
import io.why503.aiservice.global.client.dto.response.PerformanceResponse;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PerformanceRepositoryImpl implements PerformanceRepository {

    private final List<Performance> store = new ArrayList<>();

    @Override
    public void saveAll(List<Performance> performances) {
        store.clear();
        store.addAll(performances);
    }

    @Override
    public List<PerformanceResponse> findAllResponses() {
        return List.of();
    }
}