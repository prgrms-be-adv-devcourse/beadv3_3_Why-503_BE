package io.why503.aiservice.global;

import io.why503.aiservice.global.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ServiceExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> handleCustomException(CustomException e) {

        log.warn("CustomException id={}, code={}, message={}",
                e.getId(),
                e.getCode(),
                e.getMessage());

        Map<String, Object> body = Map.of(
                "id", e.getId(),
                "code", e.getCode(),
                "message", e.getMessage(),
                "status", e.getStatus().value(),
                "timestamp", LocalDateTime.now()
        );

        return ResponseEntity
                .status(e.getStatus())
                .body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {

        log.error("Unhandled Exception", e);

        Map<String, Object> body = Map.of(
                "id", "UNKNOWN",
                "code", "AI_999",
                "message", "서버 내부 오류입니다.",
                "status", 500,
                "timestamp", LocalDateTime.now()
        );

        return ResponseEntity
                .status(500)
                .body(body);
    }
}
