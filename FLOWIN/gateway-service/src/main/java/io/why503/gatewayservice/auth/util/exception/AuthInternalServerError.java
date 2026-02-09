package io.why503.gatewayservice.auth.util.exception;

import io.why503.commonbase.exception.gateway.domain.GatewayAuthException;
import org.springframework.http.HttpStatus;

public class AuthInternalServerError extends GatewayAuthException{
    public AuthInternalServerError(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    public AuthInternalServerError(Throwable cause) {
        super(cause, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
