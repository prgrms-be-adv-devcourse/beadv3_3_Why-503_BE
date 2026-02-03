package io.why503.commonbase.exception.account.domain;

import io.why503.commonbase.exception.account.AccountException;

/**
 * 여기서 두번째 코드(AC) + HttpStatus
 */
public class AccountAccountException extends AccountException {
    public AccountAccountException(String message, String status) {
        super(message, "AC-" + status);
    }
}
