package io.why503.accountservice.domain.accounts.util;

import io.why503.accountservice.domain.accounts.util.exception.AccountBadRequest;
import io.why503.accountservice.domain.accounts.util.exception.AccountNotFound;
import io.why503.commonbase.exception.CustomException;

public final class AccountExceptionFactory {

    public static CustomException accountBadRequest(String message){
        return new AccountBadRequest(message);
    }
    public static CustomException accountBadRequest(Throwable cause){
        return new AccountBadRequest(cause);
    }
    public static CustomException accountAccountNotFound(String message){
        return new AccountNotFound(message);
    }
    public static CustomException accountAccountNotFound(Throwable cause){
        return new AccountNotFound(cause);
    }
}
