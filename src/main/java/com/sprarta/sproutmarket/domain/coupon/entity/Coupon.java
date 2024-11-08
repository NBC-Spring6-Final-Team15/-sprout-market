package com.sprarta.sproutmarket.domain.coupon.entity;

import com.sprarta.sproutmarket.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String couponCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private boolean isUsed; // 쿠폰 사용 여부

    private LocalDateTime issuedAt; // 쿠폰 발급 시간

    private LocalDateTime usedAt; // 쿠폰 사용 시간 (사용된 경우)

    public Coupon(String couponCode, User user, LocalDateTime issuedAt) {
        this.couponCode = couponCode;
        this.user = user;
        this.issuedAt = issuedAt;
        this.isUsed = false;
    }

    public void useCoupon(LocalDateTime usedAt) {
        this.isUsed = true;
        this.usedAt = usedAt;
    }

}
