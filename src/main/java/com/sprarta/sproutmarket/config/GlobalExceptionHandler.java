package com.sprarta.sproutmarket.config;

import com.sprarta.sproutmarket.domain.common.ApiResponse;
import com.sprarta.sproutmarket.domain.common.dto.response.ReasonDto;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private void logError(String message) {
        log.error("예외 발생 : {} ", message);
    }

    //공통 예외 처리
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<String>> handleApiException(ApiException ex) {
        ReasonDto status = ex.getErrorCode().getReasonHttpStatus();
        logError(ex.getMessage());
        return getErrorResponse(status.getHttpStatus(), status.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiResponse<String>> handleThrowable(Throwable ex) {
        logError(ex.getMessage());
        return getErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "잠시 후 다시 시도해주십시오.");
    }

    //Valid 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        logError(ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return getErrorResponse(HttpStatus.BAD_REQUEST, ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    //파일 첨부 용량 초과 예외
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<String>> handleMaxUploadSizeExceededException() {
        HttpStatus status = HttpStatus.PAYLOAD_TOO_LARGE;
        return getErrorResponse(status, "파일 크기는 5MB를 초과할 수 없습니다.");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<String>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String errorMessage = String.format("잘못된 요청값입니다. '%s'는 유효한 %s 타입이 아닙니다.",
                ex.getValue(), Objects.requireNonNull(ex.getRequiredType()).getSimpleName());
        logError(errorMessage);
        return getErrorResponse(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleNoResourceFoundException() {
        String message = "주소를 찾을 수 없습니다.";
        logError(message);
        return getErrorResponse(HttpStatus.NOT_FOUND, message);
    }

    public ResponseEntity<ApiResponse<String>> getErrorResponse(HttpStatus status, String message) {
        return new ResponseEntity<>(ApiResponse.createError(message, status.value()), status);
    }
}
