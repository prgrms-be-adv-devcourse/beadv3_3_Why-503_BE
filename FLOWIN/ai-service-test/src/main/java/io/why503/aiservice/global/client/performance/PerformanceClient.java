package io.why503.aiservice.global.client.performance;

import io.why503.aiservice.global.client.performance.model.dto.response.PerformanceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "performance-service")
public interface PerformanceClient {

    @GetMapping("/shows")
    public List<PerformanceResponse> getShowAll();
}
