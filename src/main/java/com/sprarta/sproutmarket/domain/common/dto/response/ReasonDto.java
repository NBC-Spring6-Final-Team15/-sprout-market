package com.sprarta.sproutmarket.domain.common.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Builder
@Getter
public class ReasonDto {
    private HttpStatus httpStatus;
    private Integer statusCode;
    private String message;
}
