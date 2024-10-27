package com.sprarta.sproutmarket.domain.tradeChat.entity;

import com.sprarta.sproutmarket.domain.common.entity.Status;
import com.sprarta.sproutmarket.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id") // 채팅방 ID
    private ChatRoom chatroom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id") // 구매자 ID
    private User sender;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Builder
    private Chat(ChatRoom chatRoom, User sender, String content, Status status) {
        this.chatroom = chatRoom;
        this.sender = sender;
        this.content = content;
        this.status = status;
    }

    public void chatDelete(Status deleted) {
        this.status = deleted;
    }

}
