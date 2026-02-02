package io.why503.gatewayservice.queue.exception;

public class QueueWaitingException extends QueueException {

    private final long position;    // 내 대기 순번
    private final long total;       // 전체 대기 인원
    private final long retryAfter;  // 재요청 권장 시간 (초)

    public QueueWaitingException(
            String message,
            long position,
            long total,
            long retryAfter
    ) {
        super(message);
        this.position = position;
        this.total = total;
        this.retryAfter = retryAfter;
    }

    public long getPosition() {
        return position;
    }

    public long getTotal() {
        return total;
    }

    public long getRetryAfter() {
        return retryAfter;
    }
}
