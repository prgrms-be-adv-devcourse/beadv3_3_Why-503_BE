package io.why503.reservationservice.domain.entry.listener;

import io.why503.reservationservice.domain.queue.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EntryTokenExpiredSubscriber implements MessageListener {
    private final QueueService queueService;
    @Override
    public void onMessage(Message message, byte[] pattern) {

        String expiredKey = new String(message.getBody());

        if (!expiredKey.startsWith("entry:round:")) {
            return;
        }

        try {
            // entry:round:{roundSq}:user:{userSq}
            String[] parts = expiredKey.split(":");

            Long roundSq = Long.parseLong(parts[2]);
            Long userSq = Long.parseLong(parts[4]);

            queueService.leaveActive(roundSq, userSq);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
