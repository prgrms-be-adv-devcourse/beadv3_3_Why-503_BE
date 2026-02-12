package io.why503.gatewayservice.auth.util.exception;

import io.why503.commonbase.exception.gateway.GatewayException;
import org.springframework.http.HttpStatus;

public class InternalServerError extends GatewayException {
    public InternalServerError(String message) {
        super(message, "500", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    public InternalServerError(Throwable cause) {
        super(cause, "500", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
