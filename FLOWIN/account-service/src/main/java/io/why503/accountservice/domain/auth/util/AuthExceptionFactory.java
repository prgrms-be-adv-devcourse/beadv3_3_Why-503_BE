package io.why503.accountservice.domain.auth.util;

import io.why503.accountservice.domain.auth.util.exception.AuthBadRequest;
import io.why503.accountservice.domain.auth.util.exception.AuthUnauthorized;
import io.why503.commonbase.exception.CustomException;

public final class AuthExceptionFactory {

    public static CustomException authBadRequest(String message){
        return new AuthBadRequest(message);
    }
    public static CustomException authBadRequest(Throwable cause){
        return new AuthBadRequest(cause);
    }
    public static CustomException authUnauthorized(String message){
        return new AuthUnauthorized(message);
    }
    public static CustomException authUnauthorized(Throwable cause){
        return new AuthUnauthorized(cause);
    }

}
