package io.why503.gatewayservice.auth.util.exception;

import io.why503.commonbase.exception.gateway.domain.GatewayAuthException;
import org.springframework.http.HttpStatus;

public class AuthForbidden extends GatewayAuthException{
    public AuthForbidden(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
    public AuthForbidden(Throwable cause) {
        super(cause, HttpStatus.FORBIDDEN);
    }
}
