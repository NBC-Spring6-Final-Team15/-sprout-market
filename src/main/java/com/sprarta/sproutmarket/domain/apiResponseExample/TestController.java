package com.sprarta.sproutmarket.domain.apiResponseExample;

import com.sprarta.sproutmarket.domain.common.ApiResponse;
import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    //정상적으로 작동했을 때의 응답 예시
    @GetMapping("/test/hello")
    public ResponseEntity<ApiResponse<String>> hello() {
        return ResponseEntity.ok(ApiResponse.onSuccess("반갑습니다."));
    }

    //사용했는데 예외가 터지는 예시
    @GetMapping("/test/unhello")
    public ResponseEntity<ApiResponse<String>> unhello() {
        return ResponseEntity.ok(ApiResponse.onSuccess(throwExample()));
    }

    //예시라서 서비스 클래스 따로 안 만들고 여기서 만들었습니다.
    public String throwExample() {
        throw new ApiException(ErrorStatus.TEST_ERROR);
    }
}
