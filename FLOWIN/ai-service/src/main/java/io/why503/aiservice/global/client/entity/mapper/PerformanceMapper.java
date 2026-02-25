package io.why503.aiservice.global.client.entity.mapper;

import io.why503.aiservice.domain.ai.model.embedding.Performance;
import io.why503.aiservice.global.client.dto.response.PerformanceResponse;
import org.springframework.stereotype.Component;

@Component
public class PerformanceMapper {

    public Performance PerformanceResponseToPerformance(PerformanceResponse response) {
        return new Performance(
                response.category(),
                response.genre()
        );
    }
}