package io.why503.reservationservice.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import io.why503.reservationservice.domain.entry.listener.EntryTokenExpiredSubscriber;
import io.why503.reservationservice.domain.queue.listener.QueueEventSubscriber;

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
                new PatternTopic("queue:promote")
        );

        container.addMessageListener(
                entryTokenExpiredSubscriber,
                new PatternTopic("__keyevent@0__:expired")
        );

        return container;
    }
}