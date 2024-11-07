package com.sprarta.sproutmarket.domain.notification.aspect;

import com.sprarta.sproutmarket.domain.interestedCategory.service.InterestedCategoryService;
import com.sprarta.sproutmarket.domain.interestedItem.service.InterestedItemService;
import com.sprarta.sproutmarket.domain.item.dto.request.ItemContentsUpdateRequest;
import com.sprarta.sproutmarket.domain.item.dto.request.ItemCreateRequest;
import com.sprarta.sproutmarket.domain.item.entity.Item;
import com.sprarta.sproutmarket.domain.item.repository.ItemRepository;
import com.sprarta.sproutmarket.domain.notification.entity.PriceChangeEvent;
import com.sprarta.sproutmarket.domain.trade.dto.TradeResponseDto;
import com.sprarta.sproutmarket.domain.trade.entity.Trade;
import com.sprarta.sproutmarket.domain.trade.enums.TradeStatus;
import com.sprarta.sproutmarket.domain.trade.repository.TradeRepository;
import com.sprarta.sproutmarket.domain.user.entity.User;
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
import java.util.Map;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class NotificationAspect {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final TradeRepository tradeRepository;
    private final ItemRepository itemRepository;
    private final InterestedItemService interestedItemService;
    private final InterestedCategoryService interestedCategoryService;

    // 거래 예약 알림
    @AfterReturning(value = "execution(* com.sprarta.sproutmarket.domain.trade.service.TradeService.reserveTrade(..))", returning = "tradeResponseDto", argNames = "tradeResponseDto")
    public void sendReservationNotification(TradeResponseDto tradeResponseDto) {
        Long buyerId = tradeResponseDto.getBuyerId();
        Long sellerId = tradeResponseDto.getSellerId();

        String message = tradeResponseDto.getItemTitle() + " 예약이 완료되었습니다.";

        sendNotification(buyerId, message);
        sendNotification(sellerId, message);
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

        // 구매자와 판매자 모두에게 알림 전송
        sendNotification(trade.getChatRoom().getBuyer().getId(), message);
        sendNotification(trade.getChatRoom().getSeller().getId(), message);
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
            sendNotification(user.getId(), "새로운 물품이 관심 카테고리에 등록되었습니다: " + itemTitle);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePriceChangeEvent(PriceChangeEvent event) {
        Long itemId = event.getItemId();
        int newPrice = event.getNewPrice();

        List<User> interestedUsers = interestedItemService.findUsersByInterestedItem(itemId);

        for (User user : interestedUsers) {
            sendNotification(user.getId(), "관심 상품의 가격이 변경되었습니다. 새로운 가격: " + newPrice);
        }
    }

    private void sendNotification(Long targetId, String message) {
        simpMessagingTemplate.convertAndSend("/sub/user/" + targetId + "/notifications", message);
    }
}
