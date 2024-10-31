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

    /**
     * 거래 생성
     * @param chatRoomId 거래를 만드려고 하는 채팅방 ID
     * @param customUserDetails 인증된 판매자 유저
     * @return 거래 정보를 담은 응답 DTO
     */
    @Transactional
    public TradeResponseDto reserveTrade(Long chatRoomId, CustomUserDetails customUserDetails) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(
                () -> new ApiException(ErrorStatus.NOT_FOUND_CHATROOM));

        // 판매자와 거래 생성자가 다른 경우 예외 발생
        if (!chatRoom.getSeller().getId().equals(customUserDetails.getId())) {
            throw new ApiException(ErrorStatus.FORBIDDEN_NOT_OWNED_ITEM);
        }

        // 현재 아이템의 판매 상태가 대기중이 아닌 경우 예외 발생
        if (!chatRoom.getItem().getItemSaleStatus().equals(ItemSaleStatus.WAITING)) {
            throw new ApiException(ErrorStatus.CONFLICT_TRADE);
        }

        //거래 만들고 아이템 거래 상태 예약됨으로 변경
        Trade trade = tradeRepository.save(new Trade(chatRoom));
        chatRoom.getItem().changeSaleStatus(ItemSaleStatus.RESERVED);

        // 예약 성공 알림 전송
        sendNotification(chatRoom.getBuyer().getId(), chatRoom.getItem().getTitle() + " 예약이 완료되었습니다.");

        return TradeResponseDto.from(trade);
    }

    /**
     * 예약중인 상태를 거래 완료로 변경
     * @param tradeId 변경하고자 하는 거래 ID
     * @param customUserDetails 인증된 판매자 유저
     * @return 거래 정보를 담은 응답 DTO
     */
    @Transactional
    public TradeResponseDto finishTrade(Long tradeId, CustomUserDetails customUserDetails) {
        Trade trade = tradeRepository.findById(tradeId).orElseThrow(() ->
                new ApiException(ErrorStatus.NOT_FOUND_TRADE));

        //요청한 유저가 해당 아이템의 판매자인지 검증
        if (!trade.getChatRoom().getSeller().getId().equals(customUserDetails.getId())) {
            throw new ApiException(ErrorStatus.FORBIDDEN_NOT_SELLER);
        }

        //해당 거래가 지금 예약중 상태인지 검증
        if (!trade.getTradeStatus().equals(TradeStatus.RESERVED)) {
            throw new ApiException(ErrorStatus.CONFLICT_NOT_RESERVED);
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
