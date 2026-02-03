package io.why503.commonbase.exception.account.domain;

import io.why503.commonbase.exception.account.AccountException;

/**
 * 여기서 두번째 코드(CO) + HttpStatus
 */
public class AccountCompanyException extends AccountException {
    public AccountCompanyException(String message, String status) {
        super(message, "CO-" + status);
    }
}
