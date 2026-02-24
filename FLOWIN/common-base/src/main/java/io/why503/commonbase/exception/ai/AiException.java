package io.why503.commonbase.exception.ai;

import io.why503.commonbase.exception.CustomException;
import org.springframework.http.HttpStatus;

/**
 * 여기서 첫번째 코드(AI)
 */
public class AiException extends CustomException {
    protected AiException(String message, String code, HttpStatus status) {
        super(message, "AI-" + code, status);
    }
    protected AiException(Throwable cause, String code, HttpStatus status){
        super(cause, "AI-" + code, status);
    }
}
