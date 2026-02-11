package io.why503.gatewayservice.auth.util.exception;

import io.why503.commonbase.exception.gateway.GatewayException;
import org.springframework.http.HttpStatus;

public class ServiceUnavailable extends GatewayException {
    public ServiceUnavailable(String message) {
        super(message, "503", HttpStatus.SERVICE_UNAVAILABLE);
    }
    public ServiceUnavailable(Throwable cause) {
        super(cause, "503", HttpStatus.SERVICE_UNAVAILABLE);
    }
}
