package io.why503.gatewayservice.auth.util.exception;

import io.why503.commonbase.exception.gateway.domain.GatewayAuthException;
import org.springframework.http.HttpStatus;

public class AuthGatewayTimeout extends GatewayAuthException{
    public AuthGatewayTimeout(String message) {
        super(message, HttpStatus.GATEWAY_TIMEOUT);
    }
    public AuthGatewayTimeout(Throwable cause) {
        super(cause, HttpStatus.GATEWAY_TIMEOUT);
    }
}
