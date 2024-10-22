package com.sprarta.sproutmarket.domain.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StatusResponse {
    private String message;
    private String email;
    private Integer status;
}
