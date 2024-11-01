package com.sprarta.sproutmarket.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprarta.sproutmarket.domain.common.ApiResponse;
import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class JwtExceptionHandler {

    public static void handleJwtException(HttpServletResponse response, Exception ex) throws IOException {
        ErrorStatus errorStatus = ErrorStatus.INTERNAL_SERVER_ERROR_WE_DO_NOT_KNOW;

        if (ex instanceof ExpiredJwtException) {
            errorStatus = ErrorStatus.UNAUTHORIZED_EXPIRED_TOKEN;
        } else if (ex instanceof UnsupportedJwtException) {
            errorStatus = ErrorStatus.BAD_REQUEST_UNSUPPORTED_TOKEN;
        } else if (ex instanceof MalformedJwtException) {
            errorStatus = ErrorStatus.BAD_REQUEST_ILLEGAL_TOKEN;
        } else if (ex instanceof SignatureException) {
            errorStatus = ErrorStatus.UNAUTHORIZED_INVALID_TOKEN;
        } else if (ex instanceof IllegalArgumentException) {
            errorStatus = ErrorStatus.BAD_REQUEST_ILLEGAL_TOKEN;
        }

        response.setStatus(errorStatus.getStatusCode());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String json = new ObjectMapper().writeValueAsString(ApiResponse.createError(errorStatus.getMessage(), errorStatus.getStatusCode()));
        response.getWriter().write(json);

    }
}
