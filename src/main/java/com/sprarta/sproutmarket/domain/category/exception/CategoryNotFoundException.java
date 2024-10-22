package com.sprarta.sproutmarket.domain.category.exception;


import com.sprarta.sproutmarket.domain.common.exception.NotFoundException;

public class CategoryNotFoundException extends NotFoundException {

    private static final String MESSAGE = "카테고리를 찾을 수 없습니다.";

    public CategoryNotFoundException() {super(MESSAGE);}

    public CategoryNotFoundException(String message) {super(message);}
}
