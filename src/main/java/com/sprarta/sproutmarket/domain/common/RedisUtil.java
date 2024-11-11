package com.sprarta.sproutmarket.domain.common;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, Long> couponRedisTemplate;
    private static final String COUPON_COUNT_KEY = "coupon_count";

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public void authEmail(String key, Object object) {
        redisTemplate.opsForValue().set(key, object, 180, TimeUnit.SECONDS);
    }

    public Long incrementCouponCount() {
        return couponRedisTemplate.opsForValue().increment(COUPON_COUNT_KEY);
    }

    public Long getCouponCount() {
        Long count = couponRedisTemplate.opsForValue().get(COUPON_COUNT_KEY);
        return count != null ? count : 0L;
    }

    public void resetCouponCount() {
        couponRedisTemplate.opsForValue().set(COUPON_COUNT_KEY, 0L);
    }


}
