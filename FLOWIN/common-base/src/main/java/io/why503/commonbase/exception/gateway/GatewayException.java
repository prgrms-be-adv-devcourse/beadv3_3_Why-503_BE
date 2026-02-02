package io.why503.commonbase.exception.gateway;

import io.why503.commonbase.exception.CustomException;

/**
 * 여기서 첫번째 코드(GE)
 */
public class GatewayException extends CustomException {
    protected GatewayException(String message, String code) {
        super(message, "GE-" + code);
    }
}
