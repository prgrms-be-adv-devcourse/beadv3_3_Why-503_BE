package io.why503.commonbase.exception.gateway.domain;

import io.why503.commonbase.exception.gateway.GatewayException;
import org.springframework.http.HttpStatus;

/**
 * 여기서 두번째 코드(QU) + HttpStatus
 */
public class GatewayQueueException extends GatewayException {
    public GatewayQueueException(String message, HttpStatus status) {
        super(message, "QU-" + status.value(), status);
    }
}
