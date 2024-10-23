package com.sprarta.sproutmarket.domain.tradeChat.entity;

import org.apache.coyote.BadRequestException;

import java.util.Arrays;

public enum ChatReadStatus {
    READ, UNREAD;

    public static ChatReadStatus of(String readStatus) throws BadRequestException {
        return Arrays.stream(ChatReadStatus.values())
                .filter(r -> r.name().equalsIgnoreCase(readStatus))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("유효하지 않은 상태 값"));
    }
}
