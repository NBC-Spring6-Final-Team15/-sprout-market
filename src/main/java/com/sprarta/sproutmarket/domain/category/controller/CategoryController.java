package com.sprarta.sproutmarket.domain.category.controller;

import com.sprarta.sproutmarket.domain.category.dto.CategoryRequestDto;
import com.sprarta.sproutmarket.domain.category.dto.CategoryResponseDto;
import com.sprarta.sproutmarket.domain.category.service.CategoryService;
import com.sprarta.sproutmarket.domain.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CategoryController {
    private final CategoryService categoryService;

    /**
     * 어드민 전용, 카테고리 생성
     * 어드민 권한 인가는 필터에서 처리합니다.
     * @param requestDto : validation 사용하기 위해 dto 로 정보를 받습니다.
     * @return : id, 카테고리 이름을 담은 응답 dto
     */
    @PostMapping("/admin/category")
    public ResponseEntity<ApiResponse<CategoryResponseDto>> create(@RequestBody CategoryRequestDto requestDto) {
        CategoryResponseDto responseDto = categoryService.create(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.createSuccess("Created",201,responseDto));
    }


}
