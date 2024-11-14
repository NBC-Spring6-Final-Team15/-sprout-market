package com.sprarta.sproutmarket.domain.image.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageResponse {
    private String name;

    public ImageResponse(String name) {
        this.name = name;
    }
}
