package io.why503.accountservice.global.exception.impl;

import io.why503.accountservice.global.exception.NotFound;
import io.why503.accountservice.global.exception.ServiceUnavailable;
import io.why503.commonbase.exception.CustomException;
import io.why503.commonbase.exception.PathFinder;
import io.why503.commonbase.exception.ServiceExceptionHandler;
import io.why503.commonbase.model.dto.ExceptionResponse;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class AccountExceptionHandler extends ServiceExceptionHandler {

    protected AccountExceptionHandler(PathFinder pathFinder) {
        super(pathFinder);
    }
    //url이 없을 때
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ExceptionResponse> noHandlerFoundExceptionHandler() {
        CustomException ex = new NotFound("url not found");
        return loggingCustomException(ex);
    }
    //redis가 안켜져 있음
    @ExceptionHandler(RedisConnectionFailureException.class)
    public ResponseEntity<ExceptionResponse> redisConnectionExceptionHandler(
            RedisConnectionFailureException e
    ) {
        CustomException ex = new ServiceUnavailable(e);
        return loggingCustomException(ex);
    }

}
