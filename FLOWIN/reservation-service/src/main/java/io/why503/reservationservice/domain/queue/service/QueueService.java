package io.why503.reservationservice.domain.queue.service;

import io.why503.reservationservice.domain.queue.model.QueueResult;

public interface QueueService {

    QueueResult tryEnter(Long roundSq, Long userSq);

    void leaveActive(Long roundSq, Long userSq);

    void promoteNext(Long roundSq);
}
