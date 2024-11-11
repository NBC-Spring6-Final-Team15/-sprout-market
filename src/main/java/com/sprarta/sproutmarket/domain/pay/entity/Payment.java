package com.sprarta.sproutmarket.domain.pay.entity;

import com.sprarta.sproutmarket.domain.common.Timestamped;
import com.sprarta.sproutmarket.domain.pay.enums.PaymentMethod;
import com.sprarta.sproutmarket.domain.pay.enums.PaymentStatus;
import com.sprarta.sproutmarket.domain.trade.entity.Trade;
import jakarta.persistence.*;

@Entity
public class Payment extends Timestamped {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "payment")
    private Trade transationId;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

}
