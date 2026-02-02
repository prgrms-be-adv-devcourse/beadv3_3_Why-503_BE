package io.why503.commonbase.factory;

import io.why503.commonbase.exception.CustomException;
import io.why503.commonbase.exception.account.domain.AccountAccountException;
import io.why503.commonbase.exception.account.domain.AccountAuthException;
import io.why503.commonbase.exception.account.domain.AccountCompanyException;
import io.why503.commonbase.exception.gateway.domain.GatewayAuthException;
import io.why503.commonbase.exception.gateway.domain.GatewayQueueException;
import org.springframework.http.HttpStatus;

/**
 * 입력 값은 모두
 * String message
 * HttpStatus
 * 로 고정
 */
public final class GatewayFactory {

    private GatewayFactory(){}

    public static CustomException queueException(String message, HttpStatus status){
        return new GatewayQueueException(
                message, Integer.toString(status.value())
        );
    }
    public static CustomException authException(String message, HttpStatus status){
        return new GatewayAuthException(
                message, Integer.toString(status.value())
        );
    }
}
