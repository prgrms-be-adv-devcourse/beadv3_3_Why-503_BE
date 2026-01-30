package io.why503.aiservice.controller;


import io.why503.aiservice.model.vo.ResultRequest;
import io.why503.aiservice.model.vo.ResultResponse;
import io.why503.aiservice.service.AiService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/recommend")
public class AiController {
    private final AiService aiService;

    @PostMapping
    public ResultResponse getRecommendations(
            @RequestBody ResultRequest r
    ) {
        return aiService.getRecommendations(r);
    }
}