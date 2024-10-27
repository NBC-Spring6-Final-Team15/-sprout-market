package com.sprarta.sproutmarket.domain.tradeChat.repository;

import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import com.sprarta.sproutmarket.domain.tradeChat.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    List<Chat> findByChatroomId(Long chatroomId);

    default Chat findByIdOrElseThrow(Long chatId) {
        return findById(chatId).orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_CHAT));
    }

}
