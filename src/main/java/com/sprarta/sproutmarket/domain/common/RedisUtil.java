package com.sprarta.sproutmarket.domain.common;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public void withdraw(String key, Object object) {
        redisTemplate.opsForValue().set(key, object);
    }

    public void authEmail(String key, Object object) {
        redisTemplate.opsForValue().set(key, object, 180, TimeUnit.SECONDS);
    }

    public void contentsDelete(String key, Object object) {
        redisTemplate.opsForValue().set(key, object, 60, TimeUnit.MINUTES);
    }

    public void setLogOut(String key, Object object, Long millisecond) {
        redisTemplate.opsForValue().set(key, object, millisecond, TimeUnit.MILLISECONDS);
    }

    public boolean hasKeyLogOut(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

}
