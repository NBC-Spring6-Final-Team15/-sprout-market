package com.sprarta.sproutmarket.domain.notification.aspect;

import com.sprarta.sproutmarket.domain.trade.dto.TradeResponseDto;
import com.sprarta.sproutmarket.domain.trade.entity.Trade;
import com.sprarta.sproutmarket.domain.trade.enums.TradeStatus;
import com.sprarta.sproutmarket.domain.trade.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class NotificationAspect {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final TradeRepository tradeRepository;

    @AfterReturning(value = "execution(* com.sprarta.sproutmarket.domain.trade.service.TradeService.reserveTrade(..))", returning = "tradeResponseDto", argNames = "tradeResponseDto")
    public void sendReservationNotification(TradeResponseDto tradeResponseDto) {
        Long buyerId = tradeResponseDto.getBuyerId();
        Long sellerId = tradeResponseDto.getSellerId();

        String message = tradeResponseDto.getItemTitle() + " 예약이 완료되었습니다.";

        sendNotification(buyerId, message);
        sendNotification(sellerId, message);
    }

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

    private void sendNotification(Long targetId, String message) {
        simpMessagingTemplate.convertAndSend("/sub/user/" + targetId + "/notifications", message);
    }
}
