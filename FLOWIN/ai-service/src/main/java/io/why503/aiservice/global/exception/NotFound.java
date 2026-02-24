package io.why503.aiservice.global.exception;

import org.springframework.http.HttpStatus;

public class NotFound extends CustomException {
    public NotFound(String message) {
        super(message, "404", HttpStatus.NOT_FOUND);
    }

    public NotFound(Throwable cause) {
        super(cause, "404", HttpStatus.NOT_FOUND);
    }
}
