package io.why503.aiservice.domain.ai.util.exception;

import io.why503.commonbase.exception.ai.domain.AiAiException;
import org.springframework.http.HttpStatus;

public class AiBadRequest extends AiAiException{
    public AiBadRequest(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
    public AiBadRequest(Throwable cause) {
        super(cause, HttpStatus.BAD_REQUEST);
    }
}
