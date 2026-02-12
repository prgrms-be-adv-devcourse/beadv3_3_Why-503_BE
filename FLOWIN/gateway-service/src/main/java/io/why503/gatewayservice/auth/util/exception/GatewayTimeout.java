package io.why503.gatewayservice.auth.util.exception;

import io.why503.commonbase.exception.gateway.GatewayException;
import org.springframework.http.HttpStatus;

public class GatewayTimeout extends GatewayException {
    public GatewayTimeout(String message) {
        super(message, "504", HttpStatus.GATEWAY_TIMEOUT);
    }
    public GatewayTimeout(Throwable cause) {
        super(cause, "504", HttpStatus.GATEWAY_TIMEOUT);
    }
}
