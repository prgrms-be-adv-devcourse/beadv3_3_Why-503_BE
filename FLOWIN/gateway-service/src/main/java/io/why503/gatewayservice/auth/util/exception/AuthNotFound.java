package io.why503.gatewayservice.auth.util.exception;

import io.why503.commonbase.exception.gateway.domain.GatewayAuthException;
import org.springframework.http.HttpStatus;

public class AuthNotFound extends GatewayAuthException{
    public AuthNotFound(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
    public AuthNotFound(Throwable cause) {
        super(cause, HttpStatus.NOT_FOUND);
    }
}
