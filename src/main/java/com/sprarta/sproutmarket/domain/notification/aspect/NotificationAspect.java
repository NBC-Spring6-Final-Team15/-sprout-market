package com.sprarta.sproutmarket.domain.notification.aspect;

import com.sprarta.sproutmarket.domain.interestedCategory.service.InterestedCategoryService;
import com.sprarta.sproutmarket.domain.interestedItem.service.InterestedItemService;
import com.sprarta.sproutmarket.domain.item.dto.request.ItemCreateRequest;
import com.sprarta.sproutmarket.domain.item.repository.ItemRepository;
import com.sprarta.sproutmarket.domain.notification.entity.PriceChangeEvent;
import com.sprarta.sproutmarket.domain.notification.service.NotificationCacheService;
import com.sprarta.sproutmarket.domain.report.dto.ReportResponseDto;
import com.sprarta.sproutmarket.domain.trade.dto.TradeResponseDto;
import com.sprarta.sproutmarket.domain.trade.entity.Trade;
import com.sprarta.sproutmarket.domain.trade.enums.TradeStatus;
import com.sprarta.sproutmarket.domain.trade.repository.TradeRepository;
import com.sprarta.sproutmarket.domain.user.entity.User;
import com.sprarta.sproutmarket.domain.user.enums.UserRole;
import com.sprarta.sproutmarket.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class NotificationAspect {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final TradeRepository tradeRepository;
    private final UserRepository userRepository;
    private final InterestedItemService interestedItemService;
    private final InterestedCategoryService interestedCategoryService;
    private final NotificationCacheService notificationCacheService;

    // 거래 예약 알림
    @AfterReturning(value = "execution(* com.sprarta.sproutmarket.domain.trade.service.TradeService.reserveTrade(..))", returning = "tradeResponseDto", argNames = "tradeResponseDto")
    public void sendReservationNotification(TradeResponseDto tradeResponseDto) {
        Long buyerId = tradeResponseDto.getBuyerId();
        Long sellerId = tradeResponseDto.getSellerId();

        String message = tradeResponseDto.getItemTitle() + " 예약이 완료되었습니다.";

        notificationCacheService.saveNotification(buyerId, message);
        notificationCacheService.saveNotification(sellerId, message);

        sendNotificationForUser(buyerId, message);
        sendNotificationForUser(sellerId, message);
    }

    // 거래 상태 변경 알림
    @After(value = "execution(* com.sprarta.sproutmarket.domain.trade.service.TradeService.finishTrade(..)) && args(tradeId, customUserDetails, tradeStatus)", argNames = "joinPoint,tradeId,customUserDetails,tradeStatus")
    public void sendTradeCompletionNotification(JoinPoint joinPoint, Long tradeId, Object customUserDetails, TradeStatus tradeStatus) {
        Trade trade = tradeRepository.findByIdOrElseThrow(tradeId); // tradeId로 Trade 엔티티를 조회합니다.

        String message;

        if (tradeStatus.equals(TradeStatus.COMPLETED)) {
            message = trade.getChatRoom().getItem().getTitle() + " 거래가 완료되었습니다.";
        } else if (tradeStatus.equals(TradeStatus.CANCELLED)) {
            message = trade.getChatRoom().getItem().getTitle() + " 거래가 취소되었습니다.";
        } else {
            throw new IllegalArgumentException("잘못된 거래 상태입니다.");
        }

        // Redis 에 알림 저장
        notificationCacheService.saveNotification(trade.getChatRoom().getBuyer().getId(), message);
        notificationCacheService.saveNotification(trade.getChatRoom().getSeller().getId(), message);

        // WebSocket 으로 구매자와 판매자 모두에게 알림 전송
        sendNotificationForUser(trade.getChatRoom().getBuyer().getId(), message);
        sendNotificationForUser(trade.getChatRoom().getSeller().getId(), message);
    }

    // 새로운 아이템 등록 시 관심 카테고리 사용자에게 알림 전송
    @AfterReturning(value = "execution(* com.sprarta.sproutmarket.domain.item.service.ItemService.addItem(..))", returning = "itemResponse", argNames = "joinPoint,itemResponse")
    public void notifyUsersAboutNewItem(JoinPoint joinPoint, Object itemResponse) {
        Object[] args = joinPoint.getArgs();
        ItemCreateRequest itemCreateRequest = (ItemCreateRequest) args[0];
        Long categoryId = itemCreateRequest.getCategoryId();
        String itemTitle = itemCreateRequest.getTitle();

        List<User> interestedUsers = interestedCategoryService.findUsersByInterestedCategory(categoryId);

        for (User user : interestedUsers) {
            String message = "새로운 물품이 관심 카테고리에 등록되었습니다: " + itemTitle;

            // Redis 에 알림 저장
            notificationCacheService.saveNotification(user.getId(), message);

            // WebSocket 으로 실시간 전송
            sendNotificationForUser(user.getId(), message);
        }
    }

    // 관심 상품의 가격이 바뀌었을 때 알림 전송
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePriceChangeEvent(PriceChangeEvent event) {
        Long itemId = event.getItemId();
        int newPrice = event.getNewPrice();

        List<User> interestedUsers = interestedItemService.findUsersByInterestedItem(itemId);

        String message = "관심 상품의 가격이 변경되었습니다. 새로운 가격: " + newPrice;

        for (User user : interestedUsers) {
            // Redis 에 캐싱
            notificationCacheService.saveNotification(user.getId(), message);
            // WebSocket 으로 실시간 전송
            sendNotificationForUser(user.getId(), message);
        }
    }

    // 신고가 접수되었을 때 관리자에게 알림 전송
    @AfterReturning(value = "execution(* com.sprarta.sproutmarket.domain.report.service.ReportService.createReport(..))", returning = "reportResponseDto", argNames = "reportResponseDto")
    public void sendReportNotification(ReportResponseDto reportResponseDto) {
        List<User> adminUsers = userRepository.findAllByUserRole(UserRole.ADMIN); // ADMIN 역할을 가진 모든 사용자 조회

        String message = "새로운 신고가 접수되었습니다. " + "신고 게시몰: " + reportResponseDto.getItemId() + " 신고 사유: " + reportResponseDto.getReportingReason();

        for (User admin : adminUsers) {
            // Redis 에 저장
            notificationCacheService.saveNotification(admin.getId(), message);
            // WebSocket 으로 실시간 전송
            sendNotificationForAdmin(admin.getId(), message);

        }
    }

    private void sendNotificationForUser(Long targetId, String message) {
        simpMessagingTemplate.convertAndSend("/sub/user/" + targetId + "/notifications", message);
    }

    private void sendNotificationForAdmin(Long targetId, String message) {
        simpMessagingTemplate.convertAndSend("/sub/admin/" + targetId + "/notifications", message);
    }
}
