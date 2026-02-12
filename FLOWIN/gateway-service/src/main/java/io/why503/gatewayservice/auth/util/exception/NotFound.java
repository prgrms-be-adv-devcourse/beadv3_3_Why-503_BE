package io.why503.gatewayservice.auth.util.exception;

import io.why503.commonbase.exception.gateway.GatewayException;
import org.springframework.http.HttpStatus;

public class NotFound extends GatewayException {
    public NotFound(String message) {
        super(message, "404", HttpStatus.NOT_FOUND);
    }
    public NotFound(Throwable cause) {
        super(cause, "404", HttpStatus.NOT_FOUND);
    }
}
