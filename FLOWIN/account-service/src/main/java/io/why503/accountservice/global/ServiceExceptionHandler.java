package io.why503.accountservice.global;

import io.why503.accountservice.domain.accounts.util.AccountExceptionFactory;
import io.why503.accountservice.domain.auth.util.AuthExceptionFactory;
import io.why503.accountservice.domain.companies.util.CompanyExceptionFactory;
import io.why503.commonbase.exception.CustomException;
import io.why503.commonbase.model.dto.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ServiceExceptionHandler {
    //@Valid 예외 핸들러. 여러가지가 던져지니 그거에 맞춰서 잡을 것
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> validExceptionHandler(
            MethodArgumentNotValidException e,
            HttpServletRequest request
    ) throws Exception{
        String s = request.getRequestURI().split("/")[1];

        String message = e.getBindingResult().getFieldErrors().stream()
                .map(i -> i.getDefaultMessage())
                .collect(Collectors.joining(", ")) + "이(가) 누락되거나 옳바르지 않습니다";

        CustomException ex = makeBadRequestByURI(s, message);

        if(ex == null){
            throw new Exception(
                    "MethodArgumentNotValidException -> Exception : 없는 uri인데 MethodArgumentNotValidException"
            );
        }
        loggingCustomException(ex);
        return ResponseEntity.status(ex.getStatus())
                .body(new ExceptionResponse(ex));
    }
    //json 파싱 실패
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> JsonParseExceptionHandler(
            HttpMessageNotReadableException e,
            HttpServletRequest request
    ) throws Exception {
        String s = request.getRequestURI().split("/")[1];

        String message = "json 변환/해독 실패";

        CustomException ex = makeBadRequestByURI(s, message);

        if(ex == null){
            throw new Exception(
                    "HttpMessageNotReadableException -> Exception : 없는 uri인데 HttpMessageNotReadableException"
            );
        }
        loggingCustomException(ex);
        return ResponseEntity.status(ex.getStatus())
                .body(new ExceptionResponse(ex));
    }
    //커스텀 전체 핸들러
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ExceptionResponse> customExceptionHandler(
            CustomException e
    ){
        loggingCustomException(e);
        return ResponseEntity.status(e.getStatus())
                .body(new ExceptionResponse(e));
    }

    private void loggingCustomException(CustomException e){
        log.error("{}/{}/{}/{}/{}",
                e.getCause(),
                e.getCode(),
                e.getMessage(),
                e.getClass(),
                e.getUUID());
    }
    private CustomException makeBadRequestByURI(String s, String message){
        return switch (s) {
            case "accounts" -> AccountExceptionFactory.accountBadRequest(message);
            case "company" -> CompanyExceptionFactory.companyBadRequest(message);
            case "auth" -> AuthExceptionFactory.authBadRequest(message);
            default -> null;
        };
    }
}
