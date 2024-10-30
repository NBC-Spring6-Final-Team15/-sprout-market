package com.sprarta.sproutmarket.domain.trade.controller;

import com.sprarta.sproutmarket.domain.common.ApiResponse;
import com.sprarta.sproutmarket.domain.trade.dto.TradeResponseDto;
import com.sprarta.sproutmarket.domain.trade.service.TradeService;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TradeController {
    private final TradeService tradeService;

    /**
     * 진행중인 채팅방에서 예약 상태인 거래 생성
     * @param chatRoomId 채팅방 ID
     * @param customUserDetails 요청한 판매자 유저
     * @return 생성된 거래에 대한 정보를 담은 응답
     */
    @PostMapping("/chat-rooms/{chatRoomId}/trades")
    public ResponseEntity<ApiResponse<TradeResponseDto>> reserveTrade(
            @PathVariable Long chatRoomId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.createSuccess("Created", 201, tradeService.reserveTrade(chatRoomId, customUserDetails)));
    }

    /**
     * 예약중 상태의 거래를 거래 완료 상태로 변경
     * @param tradeId : 거래 ID
     * @param customUserDetails 요청한 판매자 유저
     * @return 수정된 거래에 대한 정보를 담은 응답
     */
    @PatchMapping("/trades/{tradeId}")
    public ResponseEntity<ApiResponse<TradeResponseDto>> finishTrade(
            @PathVariable Long tradeId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok(ApiResponse.onSuccess(tradeService.finishTrade(tradeId, customUserDetails)));
    }
}
