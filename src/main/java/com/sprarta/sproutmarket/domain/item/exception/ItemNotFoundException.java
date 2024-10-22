package com.sprarta.sproutmarket.domain.item.exception;

import com.sprarta.sproutmarket.domain.common.exception.NotFoundException;

public class ItemNotFoundException extends NotFoundException {
    private static final String MESSAGE = "매물를 찾을 수 없습니다.";

    public ItemNotFoundException() {super(MESSAGE);}

    public ItemNotFoundException(String message) {super(message);}
}
