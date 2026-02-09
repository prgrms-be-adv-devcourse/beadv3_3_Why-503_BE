package io.why503.gatewayservice.auth.util.exception;

import io.why503.commonbase.exception.gateway.GatewayException;
import io.why503.commonbase.exception.gateway.domain.GatewayAuthException;
import org.springframework.http.HttpStatus;

public class BadGateway extends GatewayException {
    public BadGateway(String message) {
        super(message, "502", HttpStatus.BAD_GATEWAY);
    }
    public BadGateway(Throwable cause) {
        super(cause, "502", HttpStatus.BAD_GATEWAY);
    }
}
