package io.why503.gatewayservice.auth.util;

import io.why503.gatewayservice.auth.util.exception.AuthUnauthorized;

public final class AuthExceptionFactory {
    public static AuthUnauthorized authUnauthorized(String message){
        return new AuthUnauthorized(message);
    }
    public static AuthUnauthorized authUnauthorized(Throwable cause){
        return new AuthUnauthorized(cause);
    }
}
