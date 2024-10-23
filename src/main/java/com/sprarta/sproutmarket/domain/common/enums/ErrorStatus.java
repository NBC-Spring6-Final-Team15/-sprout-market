package com.sprarta.sproutmarket.domain.common.enums;

import com.sprarta.sproutmarket.domain.common.BaseCode;
import com.sprarta.sproutmarket.domain.common.dto.response.ReasonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseCode {
    BAD_REQUEST_EMPTY_TITLE(HttpStatus.BAD_REQUEST, 400, "제목이 비어 있습니다."),

    //예외 예시
    BAD_REQUEST_UNSUPPORTED_TOKEN(HttpStatus.BAD_REQUEST,400,"지원되지 않는 JWT 토큰입니다."),
    BAD_REQUEST_ILLEGAL_TOKEN(HttpStatus.BAD_REQUEST,400,"잘못된 JWT 토큰입니다."),
    UNAUTHORIZED_INVALID_TOKEN(HttpStatus.UNAUTHORIZED,401,"유효하지 않는 JWT 서명입니다."),
    UNAUTHORIZED_EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED,401,"만료된 JWT 토큰입니다."),
    UNAUTHORIZED_TOKEN(HttpStatus.UNAUTHORIZED,401,"JWT 토큰 검증 중 오류가 발생했습니다."),
    FORBIDDEN_TOKEN(HttpStatus.FORBIDDEN, 403, "관리자 권한이 없습니다."),

    TEST_ERROR(HttpStatus.BAD_REQUEST, 400, "ApiException 예외 처리 테스트"),

    // user 예외처리
    BAD_REQUEST_EMAIL(HttpStatus.BAD_REQUEST, 400, "이미 존재하는 이메일입니다."),
    NOT_FOUND_AUTH_USER(HttpStatus.NOT_FOUND, 404, "가입되지 않은 유저입니다."),
    BAD_REQUEST_USER(HttpStatus.BAD_REQUEST, 400, "비활성화된 계정입니다. 관리자에 문의하세요."),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, 404, "유저를 찾을 수 없습니다."),
    BAD_REQUEST_PASSWORD(HttpStatus.BAD_REQUEST, 400, "잘못된 비밀번호입니다."),
    BAD_REQUEST_NEW_PASSWORD(HttpStatus.BAD_REQUEST, 400, "새 비밀번호는 기존 비밀번호와 같을 수 없습니다."),

    // review 예외
    NOT_FOUND_REVIEW(HttpStatus.NOT_FOUND, 404, "해당 리뷰를 찾을 수 없습니다."),
    FORBIDDEN_REVIEW_UPDATE(HttpStatus.FORBIDDEN,403,"수정할 수 있는 권한이 없습니다."),
    FORBIDDEN_REVIEW_DELETE(HttpStatus.FORBIDDEN,403,"삭제할 수 있는 권한이 없습니다."),

    // report 예외,
    NOT_FOUND_REPORT(HttpStatus.NOT_FOUND, 404, "해당 신고를 찾을 수 없습니다."),
    FORBIDDEN_REPORT_UPDATE(HttpStatus.FORBIDDEN,403,"수정할 수 있는 권한이 없습니다."),
    FORBIDDEN_REPORT_DELETE(HttpStatus.FORBIDDEN,403,"삭제할 수 있는 권한이 없습니다."),

    // trade 예외
    NOT_FOUND_TRADE(HttpStatus.NOT_FOUND, 404, "해당 거래를 찾을 수 없습니다."),

    ;

    private final HttpStatus httpStatus;
    private final Integer statusCode;
    private final String message;

    @Override
    public ReasonDto getReasonHttpStatus() {
        return ReasonDto.builder()
                .statusCode(statusCode)
                .httpStatus(httpStatus)
                .message(message)
                .build();
    }
}
