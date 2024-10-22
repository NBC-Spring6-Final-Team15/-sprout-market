package com.sprarta.sproutmarket.domain.item.exception;

import com.sprarta.sproutmarket.domain.common.exception.NotFoundException;

public class SaleStatusNotFoundException extends NotFoundException {
    private static final String MESSAGE = "판매 상태를 찾을 수 없습니다.";

    public SaleStatusNotFoundException() {super(MESSAGE);}

    public SaleStatusNotFoundException(String message) {super(message);}

}
