package io.why503.commonbase.exception.account.domain;

import io.why503.commonbase.exception.account.AccountException;
import org.springframework.http.HttpStatus;

/**
 * 여기서 두번째 코드(AC) + HttpStatus
 */
public class AccountAccountException extends AccountException {
    public AccountAccountException(String message, HttpStatus status) {
        super(message, "AC-" + status.value(), status);
    }
    public AccountAccountException(Throwable cause, HttpStatus status) {
        super(cause, "AC-" + status.value(), status);
    }
}
