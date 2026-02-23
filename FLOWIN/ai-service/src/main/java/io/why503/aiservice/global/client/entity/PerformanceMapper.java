package io.why503.aiservice.global.client.entity;

import io.why503.aiservice.domain.ai.model.embedding.Performance;
import io.why503.aiservice.global.client.dto.response.PerformanceResponse;

public interface PerformanceMapper {

    //feignClient 반환
    default Performance toDomain(PerformanceResponse response) {
        return new Performance(
                response.category(),
                response.genre()
        );
    }
}
