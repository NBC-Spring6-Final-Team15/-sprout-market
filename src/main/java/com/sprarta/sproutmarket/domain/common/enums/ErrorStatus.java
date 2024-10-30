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

    // file 에외처리
    EMPTY_FILE_EXCEPTION(HttpStatus.BAD_REQUEST, 400, "파일이 비어있습니다."),
    IO_EXCEPTION_ON_IMAGE_UPLOAD(HttpStatus.INTERNAL_SERVER_ERROR, 500, "이미지 업로드 중 오류가 발생했습니다."),
    NO_FILE_EXTENSION(HttpStatus.BAD_REQUEST, 400, "파일 확장자가 없습니다."),
    PUT_OBJECT_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, 500, "객체를 저장하는 과정에서 오류가 발생했습니다."),
    S3_SERVICE_EXCEPTION(HttpStatus.SERVICE_UNAVAILABLE, 503, "S3 서비스와의 통신 중 오류가 발생했습니다."),
    UNKNOWN_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, 500, "알 수 없는 오류가 발생했습니다."),
    INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, 400, "잘못된 파일 확장자입니다."),
    IO_EXCEPTION_ON_IMAGE_DELETE(HttpStatus.INTERNAL_SERVER_ERROR, 500, "이미지 삭제 중 오류가 발생했습니다."),

    // image 예외처리
    NOT_FOUND_IMAGE(HttpStatus.NOT_FOUND, 404, "존재하지 않는 이미지입니다."),

    // user 예외처리
    BAD_REQUEST_EMAIL(HttpStatus.NOT_FOUND, 404, "이미 존재하는 이메일입니다."),
    NOT_FOUND_AUTH_USER(HttpStatus.NOT_FOUND, 404, "가입되지 않은 유저입니다."),
    BAD_REQUEST_USER(HttpStatus.BAD_REQUEST, 404, "비활성화된 계정입니다. 관리자에 문의하세요."),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, 404, "존재하지 않는 사용자입니다."),
    BAD_REQUEST_PASSWORD(HttpStatus.BAD_REQUEST, 400, "잘못된 비밀번호입니다."),
    BAD_REQUEST_NEW_PASSWORD(HttpStatus.BAD_REQUEST, 400, "새 비밀번호는 기존 비밀번호와 같을 수 없습니다."),

    //이메일 인증 관련 예외
    FAIL_EMAIL_SENDING(HttpStatus.INTERNAL_SERVER_ERROR, 500, "이메일 전송에 실패했습니다."),
    SEND_AUTH_EMAIL(HttpStatus.OK, 200, "메일이 전송되었습니다. 인증번호와 함께 다시 요청을 보내주십시오."),
    FAIL_EMAIL_AUTHENTICATION(HttpStatus.FORBIDDEN, 403, "인증번호가 일치하지 않습니다."),
    EMAIL_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, 400, "이미 사용 중인 이메일입니다."),

    // Item
    NOT_FOUND_ITEM(HttpStatus.NOT_FOUND, 404, "존재하지 않는 아이템입니다."),
    FORBIDDEN_NOT_OWNED_ITEM(HttpStatus.FORBIDDEN, 403, "해당 매물은 로그인한 사용자의 매물이 아닙니다."),
    FORBIDDEN_NOT_SELLER(HttpStatus.FORBIDDEN, 403, "해당 물건의 판매자가 아닙니다."),
    NOT_FOUND_ITEM_SALE_STATUS(HttpStatus.NOT_FOUND, 404, "존재하지 않는 판매상태입니다."),

    // Category
    NOT_FOUND_CATEGORY(HttpStatus.NOT_FOUND, 404, "존재하지 않는 카테고리입니다."),
    BAD_REQUEST_ALREADY_EXISTS_CATETORY(HttpStatus.BAD_REQUEST, 400, "해당 카테고리는 이미 존재합니다."),
    BAD_REQUEST_SAME_NAME(HttpStatus.BAD_REQUEST,400,"이미 해당 카테고리 이름이 수정 요청된 이름과 같습니다."),

    // review 예외
    NOT_FOUND_REVIEW(HttpStatus.NOT_FOUND, 404, "해당 리뷰를 찾을 수 없습니다."),
    FORBIDDEN_REVIEW_CREATE(HttpStatus.FORBIDDEN,403,"리뷰를 생성할 수 있는 권한이 없습니다."),
    FORBIDDEN_REVIEW_UPDATE(HttpStatus.FORBIDDEN,403,"리뷰를 수정할 수 있는 권한이 없습니다."),
    FORBIDDEN_REVIEW_DELETE(HttpStatus.FORBIDDEN,403,"리뷰를 삭제할 수 있는 권한이 없습니다."),

    // report 예외,
    NOT_FOUND_REPORT(HttpStatus.NOT_FOUND, 404, "해당 신고를 찾을 수 없습니다."),
    FORBIDDEN_REPORT_UPDATE(HttpStatus.FORBIDDEN,403,"수정할 수 있는 권한이 없습니다."),
    FORBIDDEN_REPORT_DELETE(HttpStatus.FORBIDDEN,403,"삭제할 수 있는 권한이 없습니다."),

    // trade 예외
    NOT_FOUND_TRADE(HttpStatus.NOT_FOUND, 404, "해당 거래를 찾을 수 없습니다."),
    CONFLICT_TRADE(HttpStatus.CONFLICT, 409, "이미 해당 물건이 예약중이거나 거래되었습니다."),
    CONFLICT_NOT_RESERVED(HttpStatus.CONFLICT,409," 해당 거래의 상태가 예약중이 아닙니다."),

    // tradeChat 예외
    NOT_FOUND_CHATROOM(HttpStatus.NOT_FOUND, 404, "해당 채팅방을 찾을 수 없습니다."),
    FORBIDDEN_NOT_OWNED_CHATROOM(HttpStatus.FORBIDDEN, 403, "해당 채팅방은 로그인한 사용자의 채팅방이 아닙니다."),
    FORBIDDEN_CHATROOM_CREATE(HttpStatus.FORBIDDEN,403,"생성할 수 있는 권한이 없습니다."),
    CONFLICT_CHATROOM(HttpStatus.CONFLICT, 409, "이미 존재하는 채팅방입니다."),
    NOT_FOUND_CHAT(HttpStatus.NOT_FOUND, 404, "해당 채팅을 찾을 수 없습니다."),
    FORBIDDEN_NOT_OWNED_CHAT(HttpStatus.FORBIDDEN, 403, "해당 채팅은 로그인한 사용자의 채팅이 아닙니다."),


    //기타 Java 예외
    BAD_REQUEST_INVALID_FILE(HttpStatus.BAD_REQUEST,400,"업로드된 파일이 유효하지 않습니다."),

    NOT_FOUND_ADMINISTRATIVE_AREA(HttpStatus.NOT_FOUND,404,"해당 좌표로 행정구역을 찾을 수 없습니다."),

    // 관심 카테고리 예외
    ALREADY_INTERESTED_CATEGORY(HttpStatus.BAD_REQUEST, 400, "이미 관심 카테고리로 지정되었습니다."),
    NOT_FOUND_INTERESTED_CATEGORY(HttpStatus.NOT_FOUND, 404, "해당 관심 카테고리를 찾을 수 없습니다."),
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, 403, "관리자만 접근이 가능합니다."),
    ALREADY_INTERESTED_ITEM(HttpStatus.BAD_REQUEST, 400, "이미 관심 물품으로 추가되었습니다."),
    NOT_FOUND_INTERESTED_ITEM(HttpStatus.NOT_FOUND, 404, "해당 관심 물품을 찾을 수 없습니다.");

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
