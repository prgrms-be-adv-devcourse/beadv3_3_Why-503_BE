package io.why503.aiservice.global.exception.impl;

import io.why503.aiservice.domain.ai.util.exception.*;
import io.why503.commonbase.exception.CustomException;
import io.why503.commonbase.exception.PathFinder;
import io.why503.commonbase.exception.ServiceExceptionHandler;
import io.why503.commonbase.model.dto.ExceptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class AiExceptionHandler extends ServiceExceptionHandler {

    protected AiExceptionHandler(PathFinder pathFinder) {
        super(pathFinder);
    }
    //url이 없을 때
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ExceptionResponse> noHandlerFoundExceptionHandler() {
        CustomException ex = new AiNotFound("url not found");
        return loggingCustomException(ex);
    }
}
