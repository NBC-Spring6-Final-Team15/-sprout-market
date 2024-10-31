package com.sprarta.sproutmarket.domain.auth.dto.response;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KakaoProfileResponse {
    private Integer id;
    private LocalDateTime connectedAt;
    private String email;
    private String nickname;
    private String profileImage;

    public KakaoProfileResponse(String jsonResponseBody){
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(jsonResponseBody);

        this.id = element.getAsJsonObject().get("id").getAsInt();

        String connected_at = element.getAsJsonObject().get("connected_at").getAsString();
        connected_at = connected_at.substring(0, connected_at.length() - 1);
        LocalDateTime connectDateTime = LocalDateTime.parse(connected_at, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        this.connectedAt = connectDateTime;

        JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();
        this.nickname = properties.getAsJsonObject().get("nickname").getAsString();

        // profile_image 를 선택적으로 설정
        if (properties.has("profile_image") && !properties.get("profile_image").isJsonNull()) {
            this.profileImage = properties.get("profile_image").getAsString();
        }

        JsonObject kakaoAccount = element.getAsJsonObject().get("kakao_account").getAsJsonObject();
        this.email = kakaoAccount.getAsJsonObject().get("email").getAsString();
    }
}
