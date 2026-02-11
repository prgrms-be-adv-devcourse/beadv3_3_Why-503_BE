package io.why503.reservationservice.domain.queue.service;

import io.why503.reservationservice.domain.queue.model.QueueResult;

public interface QueueService {

    QueueResult tryEnter(Long roundSq, Long userSq);
    // Queueleave/QueuePromote/QueueExpire도 따지긴해야하는데 
    // 일단 입장 만들고 까먹지 않기 위한 주석임 ㅇㅇ
}
