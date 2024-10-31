package com.sprarta.sproutmarket.domain.kakao;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuthToken {
    private String access_token;
    private String refresh_token;
    private String token_type;
    private String expires_in;
}
