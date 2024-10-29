package com.sprarta.sproutmarket.domain.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import lombok.Getter;

@Getter
public class ApiResponse<T> {

    private final String message;
    private final Integer statusCode;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T data;

    private ApiResponse(String message, Integer statusCode, T data) {
        this.message = message;
        this.statusCode = statusCode;
        this.data = data;
    }

    public static <T> ApiResponse<T> createSuccess(String message, Integer statusCode, T data) {
        return new ApiResponse<>(message,statusCode,data);
    }

    public static <T> ApiResponse<T> createError(String message,Integer statusCode) {
        return new ApiResponse<>(message,statusCode,null);
    }

    public static <T> ApiResponse<T> onSuccess(T result) {
        return new ApiResponse<>("Ok", 200, result);
    }

    public static ApiResponse<String> onFailure(ErrorStatus errorStatus) {
        return new ApiResponse<>(errorStatus.getMessage(), errorStatus.getStatusCode(), null);
    }
}
