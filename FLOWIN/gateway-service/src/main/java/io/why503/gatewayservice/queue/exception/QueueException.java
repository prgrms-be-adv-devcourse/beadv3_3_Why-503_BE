package io.why503.gatewayservice.queue.exception;

public abstract class QueueException extends RuntimeException {

    protected QueueException(String message) {
        super(message);
    }
}
