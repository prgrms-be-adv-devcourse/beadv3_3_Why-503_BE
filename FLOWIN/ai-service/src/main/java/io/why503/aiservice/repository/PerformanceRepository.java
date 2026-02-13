package io.why503.aiservice.repository;

import io.why503.aiservice.model.embedding.Performance;

import java.util.List;


public interface PerformanceRepository {
    List<Performance> findAll();
    void saveAll(List<Performance> performances);
}
