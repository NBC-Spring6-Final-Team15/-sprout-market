package com.sprarta.sproutmarket.config;

import com.sprarta.sproutmarket.domain.common.ApiResponse;
import com.sprarta.sproutmarket.domain.common.dto.response.ReasonDto;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //공통 예외 처리
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<String>> handleApiException(ApiException ex) {
        ReasonDto status = ex.getErrorCode().getReasonHttpStatus();
        return getErrorResponse(status.getHttpStatus(), status.getMessage());
    }

    //파일 첨부 용량 초과 예외
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<String>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        HttpStatus status = HttpStatus.REQUEST_ENTITY_TOO_LARGE;
        return getErrorResponse(status, "파일 크기는 5MB를 초과할 수 없습니다.");
    }

    public ResponseEntity<ApiResponse<String>> getErrorResponse(HttpStatus status, String message) {

        return new ResponseEntity<>(ApiResponse.createError(message, status.value()), status);
    }
}
