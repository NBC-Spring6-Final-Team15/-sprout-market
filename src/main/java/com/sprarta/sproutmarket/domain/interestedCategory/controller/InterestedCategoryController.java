package com.sprarta.sproutmarket.domain.interestedCategory.controller;

import com.sprarta.sproutmarket.domain.common.ApiResponse;
import com.sprarta.sproutmarket.domain.interestedCategory.service.InterestedCategoryService;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import com.sprarta.sproutmarket.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class InterestedCategoryController {

    private final InterestedCategoryService interestedCategoryService;

    /**
     * 사용자가 특정 카테고리를 관심 카테고리로 등록하는 API
     * @param categoryId 관심 카테고리로 등록할 Category ID
     * @param authUser 현재 인증된 사용자 정보
     * @return ApiResponse - 관심 카테고리 등록 성공 메시지를 포함한 응답 객체
     */
    @PostMapping("/{categoryId}/interest")
    public ResponseEntity<ApiResponse<String>> addInterestedCategory(
            @PathVariable Long categoryId,
            @AuthenticationPrincipal CustomUserDetails authUser) {
        interestedCategoryService.addInterestedCategory(categoryId, authUser);
        return ResponseEntity.ok(ApiResponse.onSuccess("해당 카테고리가 관심 카테고리로 등록되었습니다."));
    }

    /**
     * 사용자가 특정 카테고리를 관심 카테고리에서 삭제하는 API
     * @param categoryId 관심 카테고리에서 삭제할 Category ID
     * @param authUser 현재 인증된 사용자 정보
     * @return ApiResponse - 관심 카테고리 삭제 성공 메시지를 포함한 응답 객체
     */
    @DeleteMapping("/{categoryId}/interest")
    public ResponseEntity<ApiResponse<String>> removeInterestedCategory(
            @PathVariable Long categoryId,
            @AuthenticationPrincipal CustomUserDetails authUser) {
        interestedCategoryService.removeInterestedCategory(categoryId, authUser);
        return ResponseEntity.ok(ApiResponse.onSuccess("해당 카테고리가 관심 카테고리에서 삭제되었습니다."));
    }

    /**
     * 특정 카테고리에 관심이 있는 사용자 목록을 조회하는 API
     * @param categoryId 관심이 있는 사용자 목록을 조회할 카테고리 ID
     * @return ApiResponse - 관심이 있는 사용자 목록
     */
    @GetMapping("/{categoryId}/interested-users")
    public ResponseEntity<ApiResponse<List<User>>> getInterestedUsers(
            @PathVariable Long categoryId) {
        List<User> interestedUsers = interestedCategoryService.findUsersByInterestedCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.onSuccess(interestedUsers));
    }
}
