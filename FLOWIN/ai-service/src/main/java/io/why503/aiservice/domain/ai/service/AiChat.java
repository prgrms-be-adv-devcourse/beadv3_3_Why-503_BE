package io.why503.aiservice.domain.ai.service;

import io.why503.aiservice.domain.ai.model.vo.AiResponse;

import java.util.concurrent.CompletableFuture;

public interface AiChat {
    CompletableFuture<String> ask(String prompt);
    AiResponse parse(String content);
    String cleanJson(String content);
}
