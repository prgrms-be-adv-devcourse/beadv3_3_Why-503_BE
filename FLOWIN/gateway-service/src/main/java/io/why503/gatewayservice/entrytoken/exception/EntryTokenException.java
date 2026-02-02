package io.why503.gatewayservice.entrytoken.exception;

public abstract class EntryTokenException extends RuntimeException {

    protected EntryTokenException(String message) {
        super(message);
    }
}
