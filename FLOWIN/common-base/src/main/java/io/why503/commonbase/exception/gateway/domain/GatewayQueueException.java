package io.why503.commonbase.exception.gateway.domain;

import io.why503.commonbase.exception.gateway.GatewayException;

/**
 * 여기서 두번째 코드(QU) + HttpStatus
 */
public class GatewayQueueException extends GatewayException {
    public GatewayQueueException(String message, String status) {
        super(message, "QU-" + status);
    }
}
