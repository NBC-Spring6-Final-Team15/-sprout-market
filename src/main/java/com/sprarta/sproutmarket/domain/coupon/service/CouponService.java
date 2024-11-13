package com.sprarta.sproutmarket.domain.coupon.service;

import com.sprarta.sproutmarket.domain.common.RedisUtil;
import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import com.sprarta.sproutmarket.domain.coupon.dto.CouponResponseDto;
import com.sprarta.sproutmarket.domain.coupon.entity.Coupon;
import com.sprarta.sproutmarket.domain.coupon.repository.CouponRepository;
import com.sprarta.sproutmarket.domain.item.entity.Item;
import com.sprarta.sproutmarket.domain.item.repository.ItemRepository;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import com.sprarta.sproutmarket.domain.user.entity.User;
import com.sprarta.sproutmarket.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponService {

    private static final int MAX_COUPONS = 100; // 쿠폰 발급 최대 수
    private static final String COUPON_LOCK_KEY = "coupon_lock"; // Redis 락 키

    private final CouponRepository couponRepository;
    private final RedisUtil redisUtil;
    private final RedisTemplate<String, Long> redisTemplate;
    private ScheduledFuture<?> scheduledFuture;
    private final RedissonClient redissonClient; // Redisson 락
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

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
    public void useCoupon(CustomUserDetails authUser, String couponCode, Long itemId) {
        Coupon coupon = couponRepository.findByCouponCodeAndUserId(couponCode, authUser.getId())
                .orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_COUPON));

        if (coupon.isUsed()) {
            throw new IllegalStateException("이미 사용된 쿠폰입니다.");
        }
        Item item = itemRepository.findByIdOrElseThrow(itemId);

        // 쿠폰 사용 처리
        coupon.useCoupon(LocalDateTime.now());

        scheduleHighlightItem(item);
    }

    // 쿠폰 코드 생성 (7자리 숫자)
    private String generateCouponCode() {
        return "COUPON-" + (1000000 + ThreadLocalRandom.current().nextInt(9000000));
    }

    // 아이템 끌어올리기 기능을 즉시 실행하고 3시간마다 반복
    private void scheduleHighlightItem(Item item) {
        final Long MAX_EXECUTIONS = 8L; // 최대 실행 횟수 설정
        String key = "executionCount:item:" + item.getId(); // Redis에서 사용할 key

        // 반복 예약
        scheduler.scheduleAtFixedRate(() -> {
            // Redis에서 실행 횟수를 가져와서 확인
            Long executionCount = redisTemplate.opsForValue().get(key);
            if (executionCount == null) {
                executionCount = 0L;
            }

            if (executionCount < MAX_EXECUTIONS) {
                highlightItem(item); // 아이템 끌어올리기 기능 실행
                redisTemplate.opsForValue().increment(key, 1); // 실행 횟수 증가
            }
            Long currentExecutionCount = redisTemplate.opsForValue().get(key);

            if (currentExecutionCount.compareTo(MAX_EXECUTIONS)==0) {
                redisTemplate.delete(key);
                scheduledFuture.cancel(false); // 개별 예약 취소
            }
        }, 0, 3, TimeUnit.HOURS); // 0초 후 즉시 실행, 이후 3시간마다 반복
    }

    @Async
    public void highlightItem(Item item) {
        item.boostItem();
        itemRepository.save(item);
    }
}
