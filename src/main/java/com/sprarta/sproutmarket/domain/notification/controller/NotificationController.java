package com.sprarta.sproutmarket.domain.notification.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class NotificationController {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 관심 목록 물품의 가격 변동 알림
     */
    public void sendPriceChangeNotification(String username, String itemName, int oldPrice, int newPrice) {
        String message = String.format("Your interested item '%s' has changed price from %d to %d.", itemName, oldPrice, newPrice);
        messagingTemplate.convertAndSendToUser(username, "/queue/notifications", message);
    }

    /**
     * 관심 카테고리의 새로운 물품 등록 알림
     */
    public void sendNewItemInCategoryNotification(String username, String categoryName, String newItemTitle) {
        String message = String.format("A new item '%s' has been added to your interested category '%s'.", newItemTitle, categoryName);
        messagingTemplate.convertAndSendToUser(username, "/queue/notifications", message);
    }

    // 만나는 일정 알림과 거래 관련 알림도 비슷한 방식으로 구현 가능합니다.
}

