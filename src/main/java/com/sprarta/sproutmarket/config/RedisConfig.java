package com.sprarta.sproutmarket.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> chatRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> chatRedisTemplate = new RedisTemplate<>(); // RedisTemplate 생성 키 String 타입 값 Object 타입

        chatRedisTemplate.setConnectionFactory(connectionFactory); // Redis 서버와 연결 관리

        chatRedisTemplate.setKeySerializer(new StringRedisSerializer()); // 키를 String 형식으로 직렬화
        chatRedisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer()); // 값 데이터 JSON 형식으로 직렬화

        return chatRedisTemplate;
    }

    @Bean
    public RedisTemplate<String, Long> RedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Long> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(connectionFactory);

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericToStringSerializer<>(Long.class)); // 단순 숫자용 직렬화

        return redisTemplate;
    }

}
