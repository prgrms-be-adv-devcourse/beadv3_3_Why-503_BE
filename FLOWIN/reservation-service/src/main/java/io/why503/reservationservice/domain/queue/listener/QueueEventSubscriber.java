package io.why503.reservationservice.domain.queue.listener;

import io.why503.reservationservice.domain.queue.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QueueEventSubscriber implements MessageListener {

    private final QueueService queueService;

    @Override
    public void onMessage(Message message, byte[] pattern) {

        String roundSqStr = new String(message.getBody());
        Long roundSq = Long.parseLong(roundSqStr);

        queueService.promoteNext(roundSq);
    }
}
