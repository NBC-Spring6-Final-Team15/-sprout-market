package com.sprarta.sproutmarket.domain.image.profileImage.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileImageResponse {
    private String name;

    public ProfileImageResponse(String name) {
        this.name = name;
    }
}
