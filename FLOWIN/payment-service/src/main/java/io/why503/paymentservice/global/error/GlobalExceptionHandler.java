package io.why503.paymentservice.global.error;

import io.why503.paymentservice.global.common.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.NoSuchElementException;

/**
 * 애플리케이션 전역에서 발생하는 예외를 포착하여 공통 에러 응답 형식으로 변환하는 클래스
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 잘못된 인자 또는 검증 실패 등 클라이언트 요청 오류 처리
    @ExceptionHandler({
            IllegalArgumentException.class,
            MethodArgumentNotValidException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception e) {
        log.warn("Bad Request: {}", e.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "BAD_REQUEST", e.getMessage());
    }

    // 존재하지 않는 자원 요청 시 예외 처리
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoSuchElementException e) {
        log.warn("Not Found: {}", e.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, "NOT_FOUND", e.getMessage());
    }

    // 비즈니스 로직 상의 상태 충돌 예외 처리
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleConflict(IllegalStateException e) {
        log.warn("Conflict: {}", e.getMessage());
        return buildErrorResponse(HttpStatus.CONFLICT, "CONFLICT", e.getMessage());
    }

    // 서버 내부에서 처리되지 않은 모든 일반 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllException(Exception e) {
        log.error("Internal Server Error: ", e);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_ERROR", "서버 내부 오류가 발생했습니다.");
    }

    // 에러 응답 객체 생성 및 반환
    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String code, String message) {
        String finalMessage = message;
        if (message != null && message.contains("Field error")) {
            finalMessage = "입력값이 올바르지 않습니다.";
        }

        return ResponseEntity.status(status)
                .body(ErrorResponse.builder()
                        .success(false)
                        .errorCode(code)
                        .message(finalMessage)
                        .build());
    }
}