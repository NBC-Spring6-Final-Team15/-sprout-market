package com.sprarta.sproutmarket.domain.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationCacheService {
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String NOTIFICATION_KEY_PREFIX = "user:%d:notifications";

    public void saveNotification(Long userId, String message) {
        String key = String.format(NOTIFICATION_KEY_PREFIX, userId);
        redisTemplate.opsForList().rightPush(key, message);
        // 알림의 TTL 설정, 3일 후 삭제
        redisTemplate.expire(key, Duration.ofDays(3));
    }

    public List<Object> getUnreadNotifications(Long userId) {
        String key = String.format(NOTIFICATION_KEY_PREFIX, userId);
        return redisTemplate.opsForList().range(key, 0, -1); // 모든 읽지 않은 알림 반환
    }

    public void markNotificationsAsRead(Long userId) {
        String key = String.format(NOTIFICATION_KEY_PREFIX, userId);
        redisTemplate.delete(key); // 읽음 처리 시 알림 삭제
    }
}
