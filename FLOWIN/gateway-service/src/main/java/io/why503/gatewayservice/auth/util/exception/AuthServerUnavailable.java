package io.why503.gatewayservice.auth.util.exception;

import io.why503.commonbase.exception.gateway.domain.GatewayAuthException;
import org.springframework.http.HttpStatus;

public class AuthServerUnavailable extends GatewayAuthException{
    public AuthServerUnavailable(String message) {
        super(message, HttpStatus.SERVICE_UNAVAILABLE);
    }
    public AuthServerUnavailable(Throwable cause) {
        super(cause, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
