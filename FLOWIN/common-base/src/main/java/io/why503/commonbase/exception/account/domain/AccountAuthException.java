package io.why503.commonbase.exception.account.domain;

import io.why503.commonbase.exception.account.AccountException;

/**
 * 여기서 두번째 코드(AU) + HttpStatus
 */
public class AccountAuthException extends AccountException {
    public AccountAuthException(String message, String status) {
        super(message, "AU-" + status);
    }
}
