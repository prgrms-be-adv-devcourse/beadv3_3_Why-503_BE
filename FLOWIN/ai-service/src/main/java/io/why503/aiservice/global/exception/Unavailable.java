package io.why503.aiservice.global.exception;

import org.springframework.http.HttpStatus;

public class Unavailable extends AiException {
    public Unavailable(String message) {
        super(message, "503", HttpStatus.SERVICE_UNAVAILABLE);
    }
    public Unavailable(Throwable cause) {
        super(cause, "503", HttpStatus.SERVICE_UNAVAILABLE);
    }
}