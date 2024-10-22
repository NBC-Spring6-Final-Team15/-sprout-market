package com.sprarta.sproutmarket.domain.common.entity;

import org.apache.coyote.BadRequestException;

import java.util.Arrays;

public enum Status {
    ACTIVE,   // 레코드가 활성화된 상태
    DELETED;

    public static Status of(String role) throws BadRequestException {
        return Arrays.stream(Status.values())
            .filter(r -> r.name().equalsIgnoreCase(role))
            .findFirst()
            .orElseThrow(() -> new BadRequestException("유효하지 않은 Status"));
    }
}
