package io.why503.accountservice.domain.accounts.util.exception;

import io.why503.commonbase.exception.account.domain.AccountAccountException;
import org.springframework.http.HttpStatus;

public class AccountConflict extends AccountAccountException {
    public AccountConflict(String message) {
        super(message, HttpStatus.CONFLICT);
    }
    public AccountConflict(Throwable cause) {
        super(cause, HttpStatus.CONFLICT);
    }
}