package io.why503.commonbase.exception.account.domain;

import io.why503.commonbase.exception.account.AccountException;
import org.springframework.http.HttpStatus;

/**
 * 여기서 두번째 코드(AU) + HttpStatus
 */
public class AccountAuthException extends AccountException {
    public AccountAuthException(String message, HttpStatus status) {
        super(message, "AU-" + status.value(), status);
    }
}
