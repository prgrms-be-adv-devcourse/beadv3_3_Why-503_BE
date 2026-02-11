package io.why503.reservationservice.domain.queue.model;

/**
 * 대기열 진입 결과
 */
public record QueueResult(
        boolean entered,   // true: 즉시 진입, false: 대기열
        Long position      // 대기열 순번 (entered=true일 경우 null)
) {

    /**
     * 즉시 진입 성공
     */
    public static QueueResult enter() {
        return new QueueResult(true, null);
    }

    /**
     * 대기열 진입
     *
     * @param position 대기열 순번 (1부터 시작)
     */
    public static QueueResult waiting(Long position) {
        return new QueueResult(false, position);
    }
}
