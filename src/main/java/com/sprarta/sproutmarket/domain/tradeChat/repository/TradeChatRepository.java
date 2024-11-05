package com.sprarta.sproutmarket.domain.tradeChat.repository;

import com.sprarta.sproutmarket.domain.tradeChat.entity.TradeChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeChatRepository extends JpaRepository<TradeChat, Long> {

    List<TradeChat> findAllByRoomId(Long roomId);

}
