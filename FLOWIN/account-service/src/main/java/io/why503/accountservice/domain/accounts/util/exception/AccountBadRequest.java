package io.why503.accountservice.domain.accounts.util.exception;

import io.why503.commonbase.exception.account.domain.AccountAccountException;
import org.springframework.http.HttpStatus;

public class AccountBadRequest extends AccountAccountException {
    public AccountBadRequest(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
    public AccountBadRequest(Throwable cause) {
        super(cause, HttpStatus.BAD_REQUEST);
    }
}
