package com.sprarta.sproutmarket.domain.image.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageUploadRequest {
    private String imageName;
    private Long userId;

    public ImageUploadRequest(String imageName, Long userId) {
        this.imageName = imageName;
        this.userId = userId;
    }
}
