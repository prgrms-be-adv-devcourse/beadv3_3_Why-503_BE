package io.why503.accountservice.domain.auth.util;

import io.why503.accountservice.domain.auth.util.exception.AuthBadRequest;

public final class AuthExceptionFactory {

    public static AuthBadRequest authBadRequest(String message){
        return new AuthBadRequest(message);
    }
    public static AuthBadRequest authBadRequest(Throwable cause){
        return new AuthBadRequest(cause);
    }

}
