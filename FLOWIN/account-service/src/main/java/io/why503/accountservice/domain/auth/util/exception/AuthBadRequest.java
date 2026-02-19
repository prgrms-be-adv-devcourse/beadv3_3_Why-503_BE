package io.why503.accountservice.domain.auth.util.exception;

import io.why503.commonbase.exception.account.domain.AccountAuthException;
import org.springframework.http.HttpStatus;

public class AuthBadRequest extends AccountAuthException {
    public AuthBadRequest(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public AuthBadRequest(Throwable cause) {
        super(cause, HttpStatus.BAD_REQUEST);
    }
}
