package io.why503.gatewayservice.auth.util.exception;

import io.why503.commonbase.exception.gateway.domain.GatewayAuthException;
import org.springframework.http.HttpStatus;

public class AuthBadGateway extends GatewayAuthException{
    public AuthBadGateway(String message) {
        super(message, HttpStatus.BAD_GATEWAY);
    }
    public AuthBadGateway(Throwable cause) {
        super(cause, HttpStatus.BAD_GATEWAY);
    }
}
