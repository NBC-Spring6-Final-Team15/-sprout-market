package com.sprarta.sproutmarket.domain.category.controller;

import com.sprarta.sproutmarket.domain.category.dto.CategoryAdminResponseDto;
import com.sprarta.sproutmarket.domain.category.dto.CategoryRequestDto;
import com.sprarta.sproutmarket.domain.category.dto.CategoryResponseDto;
import com.sprarta.sproutmarket.domain.category.service.CategoryService;
import com.sprarta.sproutmarket.domain.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @PostMapping("/admin/categories")
    public ResponseEntity<ApiResponse<CategoryResponseDto>> create(@RequestBody @Valid CategoryRequestDto requestDto) {
        CategoryResponseDto responseDto = categoryService.create(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.createSuccess("Created",201,responseDto));
    }

    /**
     * 활성화된 카테고리 전체 조회
     * @return CategoryResponseDto 를 담은 리스트
     */
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryResponseDto>>> getAll() {
        return ResponseEntity.ok(ApiResponse.onSuccess(categoryService.getActiveCategories()));
    }

    /**
     * 수정할 이름을 받아서 카테고리 이름 수정
     * @param requestDto 수정될 카테고리 이름을 담은 요청 DTO
     * @param categoryId  수정할 카테고리 ID
     * @return 수정된 카테고리 응답 DTO
     */
    @PatchMapping("/admin/categories/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponseDto>> update(@RequestBody @Valid CategoryRequestDto requestDto,
                                                                   @PathVariable Long categoryId) {
        return ResponseEntity.ok(ApiResponse.onSuccess(categoryService.update(categoryId, requestDto)));
    }

    /**
     * 카테고리 논리삭제
     * @param categoryId : 삭제처리할 카테고리 ID
     * @return 삭제처리된 카테고리 ID, 이름을 담은 String
     */
    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long categoryId) {
        categoryService.delete(categoryId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.createSuccess("No Content",204,null));
    }

    /**
     * 삭제된 카테고리 복원
     * @param categoryId 복원하고자 하는 카테고리 ID
     * @return data: null인 응답 반환
     */
    @PatchMapping("/admin/categories/deleted/{categoryId}")
    public ResponseEntity<ApiResponse<Void>> activate(@PathVariable(required = true) Long categoryId) {
        categoryService.activate(categoryId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.createSuccess("No Content",204,null));
    }

    /**
     * 삭제된 카테고리를 포함해서 전부 조회(어드민 전용)
     * @return : 삭제 상태까지 포함한 응답 Dto 리스트
     */
    @GetMapping("/admin/categories")
    public ResponseEntity<ApiResponse<List<CategoryAdminResponseDto>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.onSuccess(categoryService.getAllCategories()));
    }
}
