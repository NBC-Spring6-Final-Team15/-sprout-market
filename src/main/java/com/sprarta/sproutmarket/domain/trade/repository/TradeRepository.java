package com.sprarta.sproutmarket.domain.trade.repository;

import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import com.sprarta.sproutmarket.domain.trade.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TradeRepository extends JpaRepository<Trade, Long> {
    Optional<Trade> findByChatRoomId(Long chatRoomId);

    default Trade findByIdOrElseThrow(Long tradeId) {
        return findByChatRoomId(tradeId).orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_TRADE));
    }
}
