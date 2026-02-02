package io.why503.gatewayservice.queue.exception;

public class InvalidQueueRequestException extends QueueException {

    public InvalidQueueRequestException(String message) {
        super(message);
    }
}
