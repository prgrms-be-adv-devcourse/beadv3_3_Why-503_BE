package io.why503.commonbase.exception;

import org.springframework.http.HttpStatus;

/**
 * 커스텀 예외, 추상클래스, 보관하는 역할
 * md 파일 필독
 */
public abstract class CustomException extends RuntimeException{
    protected final String code;
    protected CustomException(String message, String code) {
        super(message);
        this.code = code;
    }
    public String getCode(){
        return code;
    }
}
