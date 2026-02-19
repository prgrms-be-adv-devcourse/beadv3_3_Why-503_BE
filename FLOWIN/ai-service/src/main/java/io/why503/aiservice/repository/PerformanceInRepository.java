package io.why503.aiservice.repository;

import io.why503.aiservice.model.embedding.Performance;
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
}