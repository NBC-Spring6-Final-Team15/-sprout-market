package com.sprarta.sproutmarket.domain.pay.repository;

import com.sprarta.sproutmarket.domain.pay.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
