package com.sprarta.sproutmarket.domain.tradeChat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class TradeChatController {


    @MessageMapping("/items/{itemId}/chat")
    public void message() {

    }

}
