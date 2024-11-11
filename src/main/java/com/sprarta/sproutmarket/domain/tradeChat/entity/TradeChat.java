package com.sprarta.sproutmarket.domain.tradeChat.entity;

import com.sprarta.sproutmarket.domain.common.Timestamped;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TradeChat extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long roomId;

    @Column(nullable = false)
    private String sender;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Long readCount = 1L; // 현재 1 대 1 채팅이기 때문에 1로 설정

    public TradeChat(String sender,
                     String content,
                     Long roomId) {
        this.sender = sender;
        this.content = content;
        this.roomId = roomId;
        this.readCount = 1L;
    }

    // 현재 1 대 1 채팅이기 때문에 한 번 확인하면 0으로
    public void decreaseReadCount() {
        this.readCount = 0L;
    }

}
