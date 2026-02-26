package io.why503.accountservice.domain.auth.util.exception;

import io.why503.commonbase.exception.account.domain.AccountAuthException;
import org.springframework.http.HttpStatus;

public class AuthUnauthorized extends AccountAuthException {
    public AuthUnauthorized(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }

    public AuthUnauthorized(Throwable cause) {
        super(cause, HttpStatus.UNAUTHORIZED);
    }
}
