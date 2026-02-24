package io.why503.commonbase.exception.ai.domain;

import io.why503.commonbase.exception.ai.AiException;
import org.springframework.http.HttpStatus;

/**
 * 여기서 두번째 코드(AI) + HttpStatus
 */
public class AiAiException extends AiException {
    public AiAiException(String message, HttpStatus status) {
        super(message, "AI-" + status.value(), status);
    }
    public AiAiException(Throwable cause, HttpStatus status) {
        super(cause, "AI-" + status.value(), status);
    }
}
