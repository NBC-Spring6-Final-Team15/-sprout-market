package com.sprarta.sproutmarket.domain.notification.controller;

import com.sprarta.sproutmarket.domain.notification.service.NotificationCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationCacheService notificationCacheService;

    // 안읽은 알림 조회
    @GetMapping("/{userId}/unread")
    public ResponseEntity<List<Object>> getUnreadNotifications(@PathVariable Long userId) {
        List<Object> unreadNotifications = notificationCacheService.getUnreadNotifications(userId);
        return ResponseEntity.ok(unreadNotifications);
    }

    // 알림 읽음 처리
    @PostMapping("/{userId}/mark-as-read")
    public ResponseEntity<Void> markNotificationsAsRead(@PathVariable Long userId) {
        notificationCacheService.markNotificationsAsRead(userId);
        return ResponseEntity.noContent().build();
    }
}