package io.why503.commonbase.exception.gateway;

import io.why503.commonbase.exception.CustomException;
import org.springframework.http.HttpStatus;

/**
 * 여기서 첫번째 코드(GE)
 */
public class GatewayException extends CustomException {
    protected GatewayException(String message, String code, HttpStatus status) {
        super(message, "GE-" + code, status);
    }
    protected GatewayException(Throwable cause, String code, HttpStatus status) {
        super(cause, "GE-" + code, status);
    }
}
