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

    private Long roomId;

    @Column(nullable = false)
    private String sender;

    @Column(nullable = false)
    private String content;

//    @Column(nullable = false)
//    private ChatReadStatus chatReadStatus;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(nullable = false, name = "chatroom_id")
//    private ChatRoom chatRoom;

    @Builder
    public TradeChat(String sender,
                     String content,
                     Long roomId) {
        this.sender = sender;
        this.content = content;
        this.roomId = roomId;
//        this.chatReadStatus = chatReadStatus;
    }

}
