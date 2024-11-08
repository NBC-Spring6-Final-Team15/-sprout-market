package com.sprarta.sproutmarket.domain.coupon.repository;

import com.sprarta.sproutmarket.domain.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByCouponCodeAndUserId(String couponCode, Long id);

}
