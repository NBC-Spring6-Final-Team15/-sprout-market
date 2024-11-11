package com.sprarta.sproutmarket.domain.coupon.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CouponResponseDto {

    private String couponCode;
    private LocalDateTime issuedAt;

}
