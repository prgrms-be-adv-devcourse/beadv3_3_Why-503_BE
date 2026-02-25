package io.why503.aiservice.domain.ai.util.exception;

import io.why503.commonbase.exception.ai.domain.AiAiException;
import org.springframework.http.HttpStatus;

public class AiForbidden extends AiAiException {
    public AiForbidden(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
    public AiForbidden(Throwable cause) {
        super(cause, HttpStatus.FORBIDDEN);
    }
}