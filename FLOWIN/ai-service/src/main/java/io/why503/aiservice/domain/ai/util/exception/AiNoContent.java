package io.why503.aiservice.domain.ai.util.exception;

import io.why503.commonbase.exception.ai.domain.AiAiException;
import org.springframework.http.HttpStatus;

public class AiNoContent extends AiAiException {
    public AiNoContent(String message) {
        super(message, HttpStatus.NO_CONTENT);
    }
    public AiNoContent(Throwable cause) {
        super(cause, HttpStatus.NO_CONTENT);
    }
}
