package io.why503.commonbase.exception.account.domain;

import io.why503.commonbase.exception.account.AccountException;
import org.springframework.http.HttpStatus;

/**
 * 여기서 두번째 코드(CO) + HttpStatus
 */
public class AccountCompanyException extends AccountException {
    public AccountCompanyException(String message, HttpStatus status) {
        super(message, "CO-" + status.value(), status);
    }
    public AccountCompanyException(Throwable cause, HttpStatus status) {
        super(cause, "CO-" + status.value(), status);
    }
}
