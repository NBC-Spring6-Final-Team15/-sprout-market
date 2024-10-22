package com.sprarta.sproutmarket.domain.tradeChat.repository;

import com.sprarta.sproutmarket.domain.tradeChat.entity.TradeChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TradeChatRepository extends JpaRepository<TradeChat, Long> {



}
