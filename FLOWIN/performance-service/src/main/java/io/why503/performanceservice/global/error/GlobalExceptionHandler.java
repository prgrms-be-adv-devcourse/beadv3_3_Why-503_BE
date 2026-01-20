package io.why503.performanceservice.global.error;

import io.why503.performanceservice.global.error.exception.PerformanceForbiddenException;
import io.why503.performanceservice.global.error.exception.UnauthorizedException;
import io.why503.performanceservice.global.error.exception.UserServiceUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(
            UnauthorizedException e
    ) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        ErrorResponse.builder()
                                .errorCode(ErrorCode.UNAUTHORIZED)
                                .message(e.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(PerformanceForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(
            PerformanceForbiddenException e
    ) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(
                        ErrorResponse.builder()
                                .errorCode(ErrorCode.PERFORMANCE_CREATE_FORBIDDEN)
                                .message(e.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(UserServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleUserServiceUnavailable(
            UserServiceUnavailableException e
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(
                        ErrorResponse.builder()
                                .errorCode(ErrorCode.USER_SERVICE_UNAVAILABLE)
                                .message(e.getMessage())
                                .build()
                );
    }
}
