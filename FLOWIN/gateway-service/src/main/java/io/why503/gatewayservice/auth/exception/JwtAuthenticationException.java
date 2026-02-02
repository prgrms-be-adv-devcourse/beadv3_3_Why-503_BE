package io.why503.gatewayservice.auth.exception;

public class JwtAuthenticationException 
            extends RuntimeException{
    public JwtAuthenticationException(String message) {
        super(message);
    }
}
