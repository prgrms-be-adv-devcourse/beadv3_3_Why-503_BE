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

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * [400 Bad Request]
     * - 잘못된 인자(IllegalArgumentException)
     * - @Valid 검증 실패
     * - Enum 변환 실패
     * - JSON 파싱 실패
     */
    @ExceptionHandler({
            IllegalArgumentException.class,
            MethodArgumentNotValidException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception e) {
        log.warn("Bad Request: {}", e.getMessage()); // 400은 warn 레벨이 적당
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "BAD_REQUEST", e.getMessage());
    }

    /**
     * [403 Forbidden]
     * - 권한 없음 (SecurityException 활용)
     */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(SecurityException e) {
        log.warn("Forbidden: {}", e.getMessage());
        return buildErrorResponse(HttpStatus.FORBIDDEN, "ACCESS_DENIED", e.getMessage());
    }

    /**
     * [404 Not Found]
     * - 자원 없음 (NoSuchElementException 활용)
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoSuchElementException e) {
        log.warn("Not Found: {}", e.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, "NOT_FOUND", e.getMessage());
    }

    /**
     * [409 Conflict]
     * - 비즈니스 상태 충돌 (IllegalStateException)
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleConflict(IllegalStateException e) {
        log.warn("Conflict: {}", e.getMessage());
        return buildErrorResponse(HttpStatus.CONFLICT, "CONFLICT", e.getMessage());
    }

    /**
     * [500 Internal Server Error]
     * - 나머지 모든 예외
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllException(Exception e) {
        log.error("Internal Server Error: ", e);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_ERROR", "서버 내부 오류가 발생했습니다.");
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String code, String message) {
        // MethodArgumentNotValidException의 경우 메시지 추출 로직이 복잡할 수 있어 단순화
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