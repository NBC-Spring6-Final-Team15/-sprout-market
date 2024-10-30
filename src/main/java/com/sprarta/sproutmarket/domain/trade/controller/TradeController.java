package com.sprarta.sproutmarket.domain.trade.controller;


import com.sprarta.sproutmarket.domain.common.ApiResponse;
import com.sprarta.sproutmarket.domain.review.dto.ReviewRequestDto;
import com.sprarta.sproutmarket.domain.review.dto.ReviewResponseDto;
import com.sprarta.sproutmarket.domain.trade.dto.TradeRequestDto;
import com.sprarta.sproutmarket.domain.trade.dto.TradeResponseDto;
import com.sprarta.sproutmarket.domain.trade.service.TradeService;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class TradeController {

    private final TradeService tradeService;

    // 예약 생성
    @PostMapping("/trades/reservations/{chatRoomId}")
    public ResponseEntity<ApiResponse<TradeResponseDto>> reserveTrade(
            @PathVariable Long chatRoomId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        TradeResponseDto responseDto = tradeService.reserveTrade(chatRoomId, customUserDetails);
        return ResponseEntity.ok(ApiResponse.onSuccess(responseDto));
    }


    // 판매 완료
    @PutMapping("/trades/{tradeId}")
    public ResponseEntity<ApiResponse<TradeResponseDto>> finishTrade(
            @PathVariable Long tradeId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        TradeResponseDto responseDto = tradeService.finishTrade(tradeId, customUserDetails);
        return ResponseEntity.ok(ApiResponse.onSuccess(responseDto));
    }


}
