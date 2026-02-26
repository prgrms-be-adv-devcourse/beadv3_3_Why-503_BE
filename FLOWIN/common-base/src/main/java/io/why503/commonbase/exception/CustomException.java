package io.why503.commonbase.exception;


import org.springframework.http.HttpStatus;

import java.util.UUID;

/**
 * 커스텀 예외, 추상클래스, 보관하는 역할
 * md 파일 필독
 */
public abstract class CustomException extends RuntimeException{
    protected final String code;
    protected final HttpStatus status;
    protected final String id;
    protected CustomException(String message, String code, HttpStatus status) {
        super(message);
        this.code = code;
        this.status = status;
        this.id = UUID.randomUUID().toString();
    }
    protected CustomException(Throwable cause, String code, HttpStatus status) {
        super(cause);
        this.code = code;
        this.status = status;
        this.id = UUID.randomUUID().toString();
    }
    public String getCode(){
        return code;
    }
    public HttpStatus getStatus(){
        return status;
    }
    public String getId(){
        return id;
    }
}
