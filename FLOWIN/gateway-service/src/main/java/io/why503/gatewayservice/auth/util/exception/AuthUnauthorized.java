package io.why503.gatewayservice.auth.util.exception;

import org.springframework.http.HttpStatus;

import io.why503.commonbase.exception.gateway.domain.GatewayAuthException;

public class AuthUnauthorized extends GatewayAuthException{
    public AuthUnauthorized(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
    public AuthUnauthorized(Throwable cause) {
        super(cause, HttpStatus.UNAUTHORIZED);
    }
}
