package io.why503.reservationservice.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import io.why503.reservationservice.domain.entry.listener.EntryTokenExpiredSubscriber;
import io.why503.reservationservice.domain.queue.listener.QueueEventSubscriber;

// Redis와 연결해서 이벤트를 수신할 수 있는 리스닝 컨테이너
/**
 * Redis = 방송국
 * RedisMessageListenerContainer = 라디오
 * Subscriber 클래스 = 라디오 듣는 사람
 * 즉 Redis에서 발생하는 publish와 TTL 만료 이벤트를 Spring리스너 클래스들에 연결
 * ★ 테스트 할 때 주의사항 ★ 
 * config get notify-keyspace-events / config set notify-keyspace-events Ex
 * 켜야 이벤트 수신가능 
 */
@Configuration
public class RedisPubSubConfig {

    @Bean
    public RedisMessageListenerContainer redisContainer(
        RedisConnectionFactory connectionFactory,
        QueueEventSubscriber subscriber,
        EntryTokenExpiredSubscriber entryTokenExpiredSubscriber
) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        container.addMessageListener(
                subscriber,
                // Ex) 누군가 Redis에 의해 queue:promote 6을 하면 자동 실행됨
                new PatternTopic("queue:promote") 
        );

        // TTL 만료 이벤트 연결
        container.addMessageListener(
                entryTokenExpiredSubscriber,
                // TTL 만료 시 내부적으로 
                // publish __keyevent@0__:expired "entry:round:{}:user:{}" 이런식으로 발생하고
                // EntryTokenExpiredSubscriber가 수신
                new PatternTopic("__keyevent@0__:expired")
        );

        return container;
    }
}