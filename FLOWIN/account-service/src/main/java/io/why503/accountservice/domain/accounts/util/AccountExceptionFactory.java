package io.why503.accountservice.domain.accounts.util;

import io.why503.accountservice.domain.accounts.util.exception.AccountBadRequest;

public final class AccountExceptionFactory {

    public static AccountBadRequest accountBadRequest(String message){
        return new AccountBadRequest(message);
    }
    public static AccountBadRequest accountBadRequest(Throwable cause){
        return new AccountBadRequest(cause);
    }

}
