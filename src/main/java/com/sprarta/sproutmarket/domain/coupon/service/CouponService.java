package com.sprarta.sproutmarket.domain.coupon.service;
import com.sprarta.sproutmarket.domain.common.RedisUtil;
import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import com.sprarta.sproutmarket.domain.coupon.dto.CouponResponseDto;
import com.sprarta.sproutmarket.domain.coupon.entity.Coupon;
import com.sprarta.sproutmarket.domain.coupon.repository.CouponRepository;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import com.sprarta.sproutmarket.domain.user.entity.User;
import com.sprarta.sproutmarket.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponService {

    private static final int MAX_COUPONS = 50; // 쿠폰 발급 최대 수
    private static final String COUPON_LOCK_KEY = "coupon_lock"; // Redis 락 키

    private final CouponRepository couponRepository;
    private final RedisUtil redisUtil;
    private final RedissonClient redissonClient; // Redisson 락
    private final UserRepository userRepository;

    // 쿠폰 발급
    public CouponResponseDto issueCoupon(CustomUserDetails authUser) {
        RLock lock = redissonClient.getLock(COUPON_LOCK_KEY);

        try {
            lock.lock(); // 락을 걸어 동시성 제어

            // 유저 정보 확인
            User user = userRepository.findById(authUser.getId())
                    .orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_USER));

            // 발급된 쿠폰 수 확인
            Long issuedCouponsCount = redisUtil.getCouponCount();
            if (issuedCouponsCount >= MAX_COUPONS) {
                throw new ApiException(ErrorStatus.CONFLICT_COUPON);
            }
            // 쿠폰 발급 수 증가
            redisUtil.incrementCouponCount();

            // 쿠폰 코드 생성
            String couponCode = generateCouponCode();

            // 쿠폰 DB에 저장
            Coupon coupon = new Coupon(couponCode, user, LocalDateTime.now());
            couponRepository.save(coupon);

            return new CouponResponseDto(coupon.getCouponCode(), coupon.getIssuedAt());
        } finally {
            lock.unlock(); // 락 해제
        }
    }

    // 쿠폰 사용
    @Transactional
    public void useCoupon(CustomUserDetails authUser, String couponCode) {
        Coupon coupon = couponRepository.findByCouponCodeAndUserId(couponCode, authUser.getId())
                .orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_COUPON));

        if (coupon.isUsed()) {
            throw new IllegalStateException("이미 사용된 쿠폰입니다.");
        }

        // 쿠폰 사용 처리
        coupon.useCoupon(LocalDateTime.now());
    }

    // 쿠폰 코드 생성 (7자리 숫자)
    private String generateCouponCode() {
        return "COUPON-" + (1000000 + ThreadLocalRandom.current().nextInt(9000000));
    }
}
