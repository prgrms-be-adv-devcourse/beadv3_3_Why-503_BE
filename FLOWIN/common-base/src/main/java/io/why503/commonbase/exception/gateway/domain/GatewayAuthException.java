package io.why503.commonbase.exception.gateway.domain;

import io.why503.commonbase.exception.gateway.GatewayException;
import org.springframework.http.HttpStatus;

/**
 * 여기서 두번째 코드(AU) + HttpStatus
 */
public class GatewayAuthException extends GatewayException {
    public GatewayAuthException(String message, HttpStatus status) {
        super(message, "AU-" + status.value(), status);
    }
}
