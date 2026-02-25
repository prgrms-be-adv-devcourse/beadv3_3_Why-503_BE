package io.why503.aiservice.global.client;

import io.why503.aiservice.domain.ai.model.embedding.ShowCategory;
import io.why503.aiservice.domain.ai.model.embedding.genre.ShowGenre;
import io.why503.aiservice.global.client.dto.response.PerformanceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "performance-service")
public interface PerformanceClient {

    @GetMapping("/shows/search")
    List<PerformanceResponse> getShowCategoryGenre(
            @RequestParam("category") ShowCategory category,
            @RequestParam("genre") ShowGenre genre
    );
    @GetMapping("/shows")
    public List<PerformanceResponse> getShowAll();
}
