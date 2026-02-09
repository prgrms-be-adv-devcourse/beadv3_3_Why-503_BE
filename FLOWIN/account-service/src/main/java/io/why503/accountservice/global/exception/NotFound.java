package io.why503.accountservice.global.exception;

import io.why503.commonbase.exception.account.AccountException;
import io.why503.commonbase.exception.account.domain.AccountCompanyException;
import org.springframework.http.HttpStatus;

public class NotFound extends AccountException {
    public NotFound(String message) {
        super(message, "404", HttpStatus.NOT_FOUND);
    }
    public NotFound(Throwable cause) {
        super(cause, "404", HttpStatus.NOT_FOUND);
    }
}
