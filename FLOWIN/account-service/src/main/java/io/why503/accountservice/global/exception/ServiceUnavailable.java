package io.why503.accountservice.global.exception;

import io.why503.commonbase.exception.account.AccountException;
import org.springframework.http.HttpStatus;

public class ServiceUnavailable extends AccountException {
    public ServiceUnavailable(String message) {
        super(message, "503", HttpStatus.SERVICE_UNAVAILABLE);
    }
    public ServiceUnavailable(Throwable cause) {
        super(cause, "503", HttpStatus.SERVICE_UNAVAILABLE);
    }
}
