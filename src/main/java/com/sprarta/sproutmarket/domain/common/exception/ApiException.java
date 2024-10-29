package com.sprarta.sproutmarket.domain.common.exception;

import com.sprarta.sproutmarket.domain.common.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApiException extends RuntimeException {
    private final BaseCode errorCode;
}
