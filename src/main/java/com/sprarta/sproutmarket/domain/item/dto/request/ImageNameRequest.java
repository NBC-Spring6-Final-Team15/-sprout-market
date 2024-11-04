package com.sprarta.sproutmarket.domain.item.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageNameRequest {
    private String imageName;
}
