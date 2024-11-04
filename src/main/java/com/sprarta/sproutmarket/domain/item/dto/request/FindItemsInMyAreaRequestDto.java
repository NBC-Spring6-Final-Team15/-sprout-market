package com.sprarta.sproutmarket.domain.item.dto.request;

import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FindItemsInMyAreaRequestDto {
    @Min(value = 1 ,message = "페이지 번호는 최소 1입니다.")
    private int page = 1;
    @Min(value = 1, message = "페이지 사이즈는 최소 1입니다.")
    private int size = 10;

    @Builder
    private FindItemsInMyAreaRequestDto(int page, int size) {
        this.page = page;
        this.size = size;
    }
}
