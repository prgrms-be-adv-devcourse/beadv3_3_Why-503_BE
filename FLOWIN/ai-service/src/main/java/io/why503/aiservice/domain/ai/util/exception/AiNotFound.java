package io.why503.aiservice.domain.ai.util.exception;

import org.springframework.http.HttpStatus;
import io.why503.commonbase.exception.ai.domain.AiAiException;

public class AiNotFound extends AiAiException {
    public AiNotFound(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
    public AiNotFound(Throwable cause) {
        super(cause, HttpStatus.NOT_FOUND);
    }
}
