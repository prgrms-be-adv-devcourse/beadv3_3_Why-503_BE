package io.why503.performanceservice.global.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

//자바는 STRING 객체로 데이터를 주지만 Redis는 바이트만 저장함
@Configuration //코드가 아닌 세팅 정보가 담긴 문서라고 알려줌
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        //RedisTemplate 객체 생성 Key는 String, Value는 Object 타입으로 지정
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        //Redis 연결 팩토리 설정 (application.yml 등의 설정 정보를 바탕으로 연결)
        //Redis 서버의 주소, 포트번호, 비밀번호 같은 연결정보가 들어있는 것을 template에 연결해줌
        template.setConnectionFactory(connectionFactory);
        //직렬화 : 데이터를 저장 가능한 형태로 변환 -> 기계어가 아닌 사람이 읽을 수 있는 글자로 저장하는 것
        // Key 직렬화 설정: StringRedisSerializer 사용
        template.setKeySerializer(new StringRedisSerializer());
        // Value 직렬화 설정: GenericJackson2JsonRedisSerializer 사용
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}