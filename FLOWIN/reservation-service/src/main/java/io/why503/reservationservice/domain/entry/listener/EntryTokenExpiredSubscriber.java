package io.why503.reservationservice.domain.entry.listener;

import io.why503.reservationservice.domain.queue.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

/**
 * 퇴장 트리거
 * 사용 기술 스택 : Redis Keyspace Notification
 *  리스너를 통해 entry Token TTL 만료 이벤트 수신
 */
@Component
@RequiredArgsConstructor
// MessageListener가 뭐냐? : Redis pub/sub 이나 keyspace 이벤트 받을수 있는 인터페이스
public class EntryTokenExpiredSubscriber implements MessageListener {
    private final QueueService queueService;
    @Override
    // TTL 만료 이벤트 감지하면 onMessage에서 트리거 해주는 역할
    public void onMessage(Message message, byte[] pattern) {

        // 만료된 Redis Key 문자열
        String expiredKey = new String(message.getBody());

        // entry token 만료가 아니다?
        if (!expiredKey.startsWith("entry:round:")) {
            return;
        }

        try {
            // 키 형식 : entry:round:{roundSq}:user:{userSq}
            String[] parts = expiredKey.split(":");

            Long roundSq = Long.parseLong(parts[2]);
            Long userSq = Long.parseLong(parts[4]);

            // TTL 만료된 유저를 active 집합에서 제거
            // leaveActive에서 promote 이벤트 발행
            queueService.leaveActive(roundSq, userSq);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
