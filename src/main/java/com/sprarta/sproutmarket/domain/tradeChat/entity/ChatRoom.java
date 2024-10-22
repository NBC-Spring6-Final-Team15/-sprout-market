package com.sprarta.sproutmarket.domain.tradeChat.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class ChatRoom{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



}
