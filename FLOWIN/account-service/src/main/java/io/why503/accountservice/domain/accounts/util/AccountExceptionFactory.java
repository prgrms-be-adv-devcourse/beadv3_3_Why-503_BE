package io.why503.accountservice.domain.accounts.util;

import io.why503.accountservice.domain.accounts.util.exception.AccountBadRequest;
import io.why503.accountservice.domain.accounts.util.exception.AccountConflict;
import io.why503.accountservice.domain.accounts.util.exception.AccountNotFound;
import io.why503.commonbase.exception.CustomException;

public final class AccountExceptionFactory {

    public static CustomException accountBadRequest(String message){
        return new AccountBadRequest(message);
    }
    public static CustomException accountBadRequest(Throwable cause){
        return new AccountBadRequest(cause);
    }
    public static CustomException accountNotFound(String message){
        return new AccountNotFound(message);
    }
    public static CustomException accountNotFound(Throwable cause){
        return new AccountNotFound(cause);
    }
    public static CustomException accountConflict(String message){
        return new AccountConflict(message);
    }
    public static CustomException accountConflict(Throwable cause){
        return new AccountConflict(cause);
    }
}
