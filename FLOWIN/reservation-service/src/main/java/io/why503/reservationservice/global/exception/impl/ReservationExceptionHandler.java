package io.why503.reservationservice.global.exception.impl;

import io.why503.commonbase.exception.CustomException;
import io.why503.commonbase.exception.PathFinder;
import io.why503.commonbase.exception.ServiceExceptionHandler;
import io.why503.commonbase.model.dto.ExceptionResponse;
import io.why503.reservationservice.global.exception.NotFound;
import io.why503.reservationservice.global.exception.ServiceUnavailable;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class ReservationExceptionHandler extends ServiceExceptionHandler {

    // 0.0.4 버전 규칙: 생성자를 통해 PathFinder 주입 후 부모 클래스로 전달
    protected ReservationExceptionHandler(PathFinder pathFinder) {
        super(pathFinder);
    }

    // 서비스에 특화된 404 URL Not Found 예외만 오버라이딩 처리
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ExceptionResponse> noHandlerFoundExceptionHandler() {
        CustomException ex = new NotFound("url not found");
        return loggingCustomException(ex);
    }

    // 서비스에 특화된 Redis 연결 예외만 오버라이딩 처리
    @ExceptionHandler(RedisConnectionFailureException.class)
    public ResponseEntity<ExceptionResponse> redisConnectionExceptionHandler(
            RedisConnectionFailureException e
    ) {
        CustomException ex = new ServiceUnavailable(e);
        return loggingCustomException(ex);
    }
}