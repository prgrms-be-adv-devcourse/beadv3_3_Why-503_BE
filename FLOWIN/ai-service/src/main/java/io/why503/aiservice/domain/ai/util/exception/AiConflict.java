package io.why503.aiservice.domain.ai.util.exception;

import io.why503.commonbase.exception.ai.domain.AiAiException;
import org.springframework.http.HttpStatus;

public class AiConflict extends AiAiException {
    public AiConflict(String message) {
        super(message, HttpStatus.CONFLICT);
    }
    public AiConflict(Throwable cause) {
        super(cause, HttpStatus.CONFLICT);
    }
}