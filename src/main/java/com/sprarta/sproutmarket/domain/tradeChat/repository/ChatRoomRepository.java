package com.sprarta.sproutmarket.domain.tradeChat.repository;

import com.sprarta.sproutmarket.domain.tradeChat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {



}
