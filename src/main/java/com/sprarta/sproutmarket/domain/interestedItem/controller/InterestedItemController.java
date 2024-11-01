package com.sprarta.sproutmarket.domain.interestedItem.controller;

import com.sprarta.sproutmarket.domain.common.ApiResponse;
import com.sprarta.sproutmarket.domain.interestedItem.service.InterestedItemService;
import com.sprarta.sproutmarket.domain.item.dto.response.ItemResponseDto;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class InterestedItemController {
    private final InterestedItemService interestedItemService;

    /**
     * 사용자가 특정 상품을 관심 상품 리스트에 등록하는 API
     * @param itemId 관심 상품으로 등록할 Item의 ID
     * @param authUser 현재 인증된 사용자 정보
     * @return ApiResponse - 관심 상품 등록 성공 메시지를 포함한 응답 객체
     */
    @PostMapping("/{itemId}/interest")
    public ResponseEntity<ApiResponse<Void>> addInterestedItem(
            @PathVariable Long itemId,
            @AuthenticationPrincipal CustomUserDetails authUser) {
        interestedItemService.addInterestedItem(itemId, authUser);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    /**
     * 사용자가 특정 상품을 관심 상품 리스트에서 삭제하는 API
     * @param itemId 관심 상품에서 삭제할 Item의 ID
     * @param authUser 현재 인증된 사용자 정보
     * @return ApiResponse - 관심 상품 삭제 성공 메시지를 포함한 응답 객체
     */
    @DeleteMapping("/{itemId}/interest")
    public ResponseEntity<ApiResponse<Void>> removeInterestedItem(@PathVariable Long itemId, @AuthenticationPrincipal CustomUserDetails authUser) {
        interestedItemService.removeInterestedItem(itemId, authUser);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    /**
     * 현재 사용자의 관심 상품을 페이지네이션으로 조회하는 API
     * @param page 페이지 번호(1부터 시작)
     * @param size 페이지당 조회할 관심 상품 개수
     * @param authUser 현재 인증된 사용자 정보
     * @return ApiResponse - 페이지네이션된 관심 상품 목록을 포함한 응답 객체
     */
    @GetMapping("/interested")
    public ResponseEntity<ApiResponse<Page<ItemResponseDto>>> getInterestedItems(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails authUser) {

        Page<ItemResponseDto> interestedItemsPage = interestedItemService.getInterestedItems(authUser, page, size);
        return ResponseEntity.ok(ApiResponse.onSuccess(interestedItemsPage));
    }
}
