package io.why503.commonbase.exception.account;

import io.why503.commonbase.exception.CustomException;
import org.springframework.http.HttpStatus;

/**
 * 여기서 첫번째 코드(AC)
 */
public class AccountException extends CustomException {
    protected AccountException(String message, String code, HttpStatus status) {
        super(message, "AC-" + code, status);
    }
    protected AccountException(Throwable cause, String code, HttpStatus status){
        super(cause, "AC-" + code, status);
    }
}
