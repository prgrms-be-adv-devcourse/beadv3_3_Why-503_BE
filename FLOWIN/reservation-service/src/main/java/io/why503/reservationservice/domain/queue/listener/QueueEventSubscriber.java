package io.why503.reservationservice.domain.queue.listener;

import io.why503.reservationservice.domain.queue.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

/**
 * 승격(입장) 트리거 - active 자리가 비었을때 다음 대기자 자동으로 승격
 * 사용 기술 스택 : Redis Keyspace Notification
 *  리스너를 통해 Redis Pub/Sub의 승격을 수신
 */
@Component
@RequiredArgsConstructor
public class QueueEventSubscriber implements MessageListener {

    private final QueueService queueService;

    @Override
    public void onMessage(Message message, byte[] pattern) {

        // Redis 채널에 전달된 roundSq값 가져오기
        String roundSqStr = new String(message.getBody());
        Long roundSq = Long.parseLong(roundSqStr);

        // 해당 round의 대기열에서 다음 유저를 승격시켜주는 로직
        queueService.promoteNext(roundSq);
    }
}
