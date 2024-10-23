package com.sprarta.sproutmarket.domain.item.controller;

import com.sprarta.sproutmarket.domain.common.ApiResponse;
import com.sprarta.sproutmarket.domain.item.dto.request.ItemContentsUpdateRequest;
import com.sprarta.sproutmarket.domain.item.dto.request.ItemCreateRequest;
import com.sprarta.sproutmarket.domain.item.dto.response.ItemResponse;
import com.sprarta.sproutmarket.domain.item.service.ItemService;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;


    /**
     * 로그인한 사용자가 중고 물품을 등록하는 로직
     * @param itemCreateRequest 매물 세부 정보를 포함한 요청 객체(제목, 설명, 가격, 카테고리id)
     * @param authUser 매물 등록을 요청한 사용자
     * @return ApiResponse - 생성된 아이템에 대한 메세지, 상태 코드를 포함한 응답 객체
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ItemResponse>> createItem(@RequestBody ItemCreateRequest itemCreateRequest, @AuthenticationPrincipal CustomUserDetails authUser){
        ItemResponse itemResponse = itemService.createItem(itemCreateRequest, authUser);
        return ResponseEntity.ok(ApiResponse.onSuccess(itemResponse));
    }

    /**
     * 매물의 판매 상태만을 변경하는 로직
     * @param itemId Item's ID
     * @param saleStatus Item's 판매 상태
     * @param authUser 매물 판매 상태 수정을 요청한 사용자
     * @return ApiResponse - 판매상태가 수정된 아이템에 대한 메세지, 상태 코드를 포함한 응답 객체
     */
    @PostMapping("/{itemId}/update/sale-status")
    public ResponseEntity<ApiResponse<ItemResponse>> updateItemSaleStatus(@PathVariable Long itemId, @RequestParam String saleStatus, @AuthenticationPrincipal CustomUserDetails authUser){
        ItemResponse itemResponse = itemService.updateSaleStatus(itemId, saleStatus, authUser);
        return ResponseEntity.ok(ApiResponse.onSuccess(itemResponse));
    }

    /**
     * 매물의 내용(제목, 설명, 가격, 이미지URL)을 수정하는 로직
     * @param itemId Item's ID
     * @param itemContentsUpdateRequest 매물 수정 정보를 포함한 요청 객체(제목, 내용, 가격, 이미지URL)
     * @param authUser 매물 내용 수정을 요청한 사용자
     * @return ApiResponse - 판매상태가 수정된 아이템에 대한 메세지, 상태 코드를 포함한 응답 객체
     */
    @PostMapping("/{itemId}/update/contents")
    public ResponseEntity<ApiResponse<ItemResponse>> updateContents(@PathVariable Long itemId, @RequestBody ItemContentsUpdateRequest itemContentsUpdateRequest, @AuthenticationPrincipal CustomUserDetails authUser){
        ItemResponse itemResponse = itemService.updateContents(itemId, itemContentsUpdateRequest, authUser);
        return ResponseEntity.ok(ApiResponse.onSuccess(itemResponse));
    }

    /**
     * 매물을 삭제하는 로직
     * @param itemId
     * @param authUser
     * @return
     */
    @PostMapping("/{itemId}/delete")
    public ResponseEntity<ApiResponse<ItemResponse>> solfDeleteItem(@PathVariable Long itemId, @AuthenticationPrincipal CustomUserDetails authUser){
        ItemResponse itemResponse = itemService.softDeleteItem(itemId, authUser);
        return ResponseEntity.ok(ApiResponse.onSuccess(itemResponse));
    }

}
