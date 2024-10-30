package com.sprarta.sproutmarket.domain.trade.service;


import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import com.sprarta.sproutmarket.domain.item.entity.ItemSaleStatus;
import com.sprarta.sproutmarket.domain.trade.dto.TradeResponseDto;
import com.sprarta.sproutmarket.domain.trade.entity.Trade;
import com.sprarta.sproutmarket.domain.trade.enums.TradeStatus;
import com.sprarta.sproutmarket.domain.trade.repository.TradeRepository;
import com.sprarta.sproutmarket.domain.tradeChat.entity.ChatRoom;
import com.sprarta.sproutmarket.domain.tradeChat.repository.ChatRoomRepository;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class TradeService {

    private final TradeRepository tradeRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatRoomRepository chatRoomRepository;

    // 예약
    /*
    채팅방 가져오기
    요청한 사람이 채팅방의 seller인지 확인
    현재 아이템 상태가 WAITING인지 확인
    거래 생성
    아이템 예약 상태로 변경
     */
    @Transactional
    public TradeResponseDto reserveTrade(Long chatRoomId, CustomUserDetails customUserDetails) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(
                () -> new ApiException(ErrorStatus.NOT_FOUND_CHATROOM));

        if (!chatRoom.getSeller().getId().equals(customUserDetails.getId())) {
            throw new ApiException(ErrorStatus.FORBIDDEN_NOT_OWNED_ITEM);
        }

        if (!chatRoom.getItem().getItemSaleStatus().equals(ItemSaleStatus.WAITING)) {
            throw new ApiException(ErrorStatus.BAD_REQUEST_CONFLICT_TRADE);
        }

        //거래 만들고 아이템 거래 상태 예약됨으로 변경
        Trade trade = tradeRepository.save(new Trade(chatRoom));
        chatRoom.getItem().changeSaleStatus(ItemSaleStatus.RESERVED);

        // 예약 성공 알림 전송
        sendNotification(chatRoom.getBuyer().getId(), chatRoom.getItem().getTitle() + " 예약이 완료되었습니다.");

        return TradeResponseDto.from(trade);
    }

    // 예약 -> 판매완료로 변경
    @Transactional
    public TradeResponseDto finishTrade(Long tradeId, CustomUserDetails customUserDetails) {
        Trade trade = tradeRepository.findById(tradeId).orElseThrow(() ->
                new ApiException(ErrorStatus.NOT_FOUND_TRADE));

        if (!trade.getChatRoom().getSeller().getId().equals(customUserDetails.getId())) {
            throw new ApiException(ErrorStatus.FORBIDDEN_NOT_SELLER);
        }

        if (!trade.getTradeStatus().equals(TradeStatus.RESERVED)) {
            throw new ApiException(ErrorStatus.BAD_REQUEST_NOT_RESERVED);
        }

        trade.updateTradeStatus(TradeStatus.COMPLETED);
        trade.getChatRoom().getItem().changeSaleStatus(ItemSaleStatus.SOLD);

        // 거래 완료 알림 전송 (구매자와 판매자 모두에게)
        String message = trade.getChatRoom().getItem().getTitle() + " 거래가 완료되었습니다.";
        sendNotification(trade.getChatRoom().getBuyer().getId(), message);
        sendNotification(trade.getChatRoom().getSeller().getId(), message);

        return TradeResponseDto.from(trade);
    }

    private void sendNotification(Long targetId, String message) {
        simpMessagingTemplate.convertAndSend("/sub/user/" + targetId + "/notifications", message);
    }
}
