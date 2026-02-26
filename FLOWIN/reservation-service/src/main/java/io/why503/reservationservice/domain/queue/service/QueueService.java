package io.why503.reservationservice.domain.queue.service;

import io.why503.reservationservice.domain.queue.model.QueueResult;
import io.why503.reservationservice.domain.queue.model.QueueStatusResponse;

public interface QueueService {

    // 대기열 진입 시도
    QueueResult tryEnter(Long roundSq, Long userSq);

    // active 유저 나갈때 호출
    void leaveActive(Long roundSq, Long userSq);

    // 다음 대기자 승격
    void promoteNext(Long roundSq);

    // UI 상태 조회용
    QueueStatusResponse getStatus(Long roundSq, Long userSq);
}
