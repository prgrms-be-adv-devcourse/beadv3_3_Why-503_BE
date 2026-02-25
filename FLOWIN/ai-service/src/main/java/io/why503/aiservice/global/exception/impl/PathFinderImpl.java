package io.why503.aiservice.global.exception.impl;

import io.why503.commonbase.exception.CustomException;
import io.why503.commonbase.exception.PathFinder;
import io.why503.commonbase.exception.ai.domain.AiAiException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class PathFinderImpl implements PathFinder {
    @Override
    public CustomException findPath(HttpServletRequest request, String message, HttpStatus status) {
        String s = request.getRequestURI().split("/")[1];
        return switch (s) {
            case "ai" -> new AiAiException(message, status);
            default -> null;
        };
    }
}
