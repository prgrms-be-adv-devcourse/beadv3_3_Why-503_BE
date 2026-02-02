package io.why503.performanceservice.global.error;

import io.why503.performanceservice.global.error.exception.BusinessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 비즈니스 로직 예외 처리
    // ErrorCode에 정의된 상태코드(400, 401, 403, 404, 409, 502 등)로 응답
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorResponse.builder()
                        .errorCode(errorCode)
                        .message(errorCode.getMessage())
                        .build()
                );
    }

    // @Valid 유효성 검사 실패
    // HTTP 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    // 동시성 이슈 (낙관적 락 충돌)
    // HTTP 409
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLockException(ObjectOptimisticLockingFailureException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorResponse.builder()
                        .errorCode(ErrorCode.SEAT_ALREADY_SELECTED)
                        .message("선택한 좌석이 이미 다른 사용자에 의해 변경되었습니다. 다시 시도해주세요.")
                        .build()
                );
    }

    // DB 무결성 위반 (중복 키, 필수 값 누락 등)
    // HTTP 409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorResponse.builder()
                        .errorCode(ErrorCode.INTERNAL_SERVER_ERROR)
                        .message("데이터 처리 중 충돌이 발생했습니다.")
                        .build()
                );
    }

    // 그 외 예상치 못한 모든 에러
    // HTTP 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        e.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.builder()
                        .errorCode(ErrorCode.INTERNAL_SERVER_ERROR)
                        .message("서버 내부 오류가 발생했습니다.")
                        .build()
                );
    }
}