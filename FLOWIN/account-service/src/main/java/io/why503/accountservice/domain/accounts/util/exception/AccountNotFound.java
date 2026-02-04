package io.why503.accountservice.domain.accounts.util.exception;

import io.why503.commonbase.exception.account.domain.AccountAccountException;
import org.springframework.http.HttpStatus;

public class AccountNotFound extends AccountAccountException {
    public AccountNotFound(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
    public AccountNotFound(Throwable cause) {
        super(cause, HttpStatus.NOT_FOUND);
    }
}
