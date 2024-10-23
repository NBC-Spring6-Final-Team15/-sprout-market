package com.sprarta.sproutmarket.domain.trade.repository;

import com.sprarta.sproutmarket.domain.trade.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeRepository extends JpaRepository<Trade, Long> {
}
