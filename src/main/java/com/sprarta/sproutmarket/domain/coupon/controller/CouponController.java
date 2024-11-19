package com.sprarta.sproutmarket.domain.coupon.controller;

import com.sprarta.sproutmarket.domain.common.ApiResponse;
import com.sprarta.sproutmarket.domain.coupon.dto.CouponResponseDto;
import com.sprarta.sproutmarket.domain.coupon.service.CouponService;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;


    // 쿠폰 발급
    @PostMapping("/coupons/issue")
    public ResponseEntity<ApiResponse<CouponResponseDto>> issueCoupon(
            @AuthenticationPrincipal CustomUserDetails authUser
    ) {
        CouponResponseDto responseDto = couponService.issueCoupon(authUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.createSuccess("Created", 201, responseDto));
    }

    // 쿠폰 사용
    @PostMapping("/coupons/use")
    public ResponseEntity<ApiResponse<Void>> useCoupon(
            @AuthenticationPrincipal CustomUserDetails authUser,
            @RequestParam String couponCode,
            @RequestParam Long itemId
    ) {
        couponService.useCoupon(authUser, couponCode, itemId);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }
}