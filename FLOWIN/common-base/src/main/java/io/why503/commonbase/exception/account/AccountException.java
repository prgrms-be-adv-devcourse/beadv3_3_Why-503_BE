package io.why503.commonbase.exception.account;

import io.why503.commonbase.exception.CustomException;

/**
 * 여기서 첫번째 코드(AC)
 */
public class AccountException extends CustomException {
    protected AccountException(String message, String code) {
        super(message, "AC-" + code);
    }
}
