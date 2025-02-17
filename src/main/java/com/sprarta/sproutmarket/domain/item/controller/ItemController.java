package com.sprarta.sproutmarket.domain.item.controller;

import com.sprarta.sproutmarket.domain.common.ApiResponse;
import com.sprarta.sproutmarket.domain.item.dto.request.FindItemsInMyAreaRequestDto;
import com.sprarta.sproutmarket.domain.item.dto.request.ItemContentsUpdateRequest;
import com.sprarta.sproutmarket.domain.item.dto.request.ItemCreateRequest;
import com.sprarta.sproutmarket.domain.item.dto.request.ItemSearchRequest;
import com.sprarta.sproutmarket.domain.item.dto.response.ItemResponse;
import com.sprarta.sproutmarket.domain.item.dto.response.ItemResponseDto;
import com.sprarta.sproutmarket.domain.item.dto.response.ItemSearchResponse;
import com.sprarta.sproutmarket.domain.item.entity.ItemSaleStatus;
import com.sprarta.sproutmarket.domain.item.service.ItemService;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    /**
     * 중고 매물에 대해서 검색하는 로직
     * @param page 페이지당 카드 수
     * @param size 현재 인증된 사용자 정보
     * @param itemSearchRequest 매물 검색 조건을 포함한 요청 객체(키워드, 카테고리id, 판매상태)
     * @param authUser 매물 수정을 요청한 사용자
     * @return ApiResponse - 메세지, 상태 코드, 조건에 해당하는 매물의 상세 정보를 포함한 응답 객체
     */
    @PostMapping("/items/search")
    public ResponseEntity<ApiResponse<Page<ItemSearchResponse>>> searchItems(@RequestParam(name = "page", defaultValue = "1") int page,
                                                                             @RequestParam(name = "size", defaultValue = "10") int size,
                                                                             @RequestBody ItemSearchRequest itemSearchRequest,
                                                                             @AuthenticationPrincipal CustomUserDetails authUser){
        return ResponseEntity.ok(ApiResponse.onSuccess(itemService.searchItems(page, size, itemSearchRequest, authUser)));
    }

    /**
     * 로그인한 사용자가 중고 물품을 등록하는 로직
     * @param itemCreateRequest 매물 세부 정보를 포함한 요청 객체(제목, 설명, 가격, 카테고리id)
     * @param authUser 매물 등록을 요청한 사용자
     * @return ApiResponse - 생성된 아이템에 대한 메세지, 상태 코드를 포함한 응답 객체
     */
    @PostMapping("/items")
    public ResponseEntity<ApiResponse<ItemResponse>> addItem(@RequestBody ItemCreateRequest itemCreateRequest, @AuthenticationPrincipal CustomUserDetails authUser){
        ItemResponse itemResponse = itemService.addItem(itemCreateRequest, authUser);
        return ResponseEntity.ok(ApiResponse.onSuccess(itemResponse));
    }

    /**
     * 매물의 판매 상태만을 변경하는 로직
     * @param itemId Item's ID
     * @param saleStatus Item's 판매 상태
     * @param authUser 매물 판매 상태 수정을 요청한 사용자
     * @return ApiResponse - 판매상태가 수정된 아이템에 대한 메세지, 상태 코드를 포함한 응답 객체
     */
    @PutMapping("/items/{itemId}/sale-status")
    public ResponseEntity<ApiResponse<Void>> updateItemSaleStatus(@PathVariable(name = "itemId") Long itemId, @RequestParam ItemSaleStatus saleStatus, @AuthenticationPrincipal CustomUserDetails authUser){
        itemService.updateSaleStatus(itemId, saleStatus, authUser);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    /**
     * 매물의 내용(제목, 설명, 가격, 이미지URL)을 수정하는 로직
     * @param itemId Item's ID
     * @param itemContentsUpdateRequest 매물 수정 정보를 포함한 요청 객체(제목, 내용, 가격, 이미지URL)
     * @param authUser 매물 내용 수정을 요청한 사용자
     * @return ApiResponse - 내용이 수정된 아이템에 대한 정보, 메세지, 상태 코드를 포함한 응답 객체
     */
    @PutMapping("/items/{itemId}/contents")
    public ResponseEntity<ApiResponse<ItemResponse>> updateContent(@PathVariable(name = "itemId") Long itemId, @RequestBody ItemContentsUpdateRequest itemContentsUpdateRequest, @AuthenticationPrincipal CustomUserDetails authUser){
        ItemResponse itemResponse = itemService.updateContents(itemId, itemContentsUpdateRequest, authUser);
        return ResponseEntity.ok(ApiResponse.onSuccess(itemResponse));
    }

    /**
     * 자신이 등록한 매물을 (논리적)삭제하는 로직
     * @param itemId Item's ID
     * @param authUser 매물 내용 수정을 요청한 사용자
     * @return ApiResponse - 메세지, 상태 코드, 삭제한 아이템에 대한 정보를 포함한 응답 객체
     */
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<Void>> softRemoveItem(@PathVariable(name = "itemId") Long itemId, @AuthenticationPrincipal CustomUserDetails authUser){
        itemService.softDeleteItem(itemId, authUser);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    /**
     * 관리자가 신고된 매물을 (논리적)삭제하는 로직
     * @param itemId Item's ID
     * @return ApiResponse - 메세지, 상태 코드, 삭제된 아이템에 대한 정보를 포함한 응답 객체
     */
    @DeleteMapping("/admin/items/{itemId}")
    public ResponseEntity<ApiResponse<ItemResponse>> softRemoveReportedItem(@PathVariable(name = "itemId") Long itemId){
        itemService.softDeleteReportedItem(itemId);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    /**
     * 로그인 한 사용자가 특정 매물을 상세조회하는 로직
     * @param itemId Item's ID
     * @return ApiResponse - 메세지, 상태 코드, 아이템의 상세 정보를 포함한 응답 객체
     */
    @GetMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<ItemResponseDto>> findItem(@PathVariable(name = "itemId") Long itemId, @AuthenticationPrincipal CustomUserDetails authUser){
        ItemResponseDto itemResponseDto = itemService.getItem(itemId, authUser);
        return ResponseEntity.ok(ApiResponse.onSuccess(itemResponseDto));
    }

    /**
     * 현재 인증된 사용자의 모든 매물을 조회하는 로직
     * @param page 페이지 번호(1부터 시작)
     * @param size 페이지당 카드 수
     * @param authUser 현재 인증된 사용자 정보
     * @return ApiResponse - 메세지, 상태 코드, 로그인한 사용자의 모든 매물 상세 정보를 포함한 응답 객체
     */
    @GetMapping("/items/mine")
    public ResponseEntity<ApiResponse<Page<ItemResponseDto>>> findMyItems(@RequestParam(name = "page", defaultValue = "1") int page,
                                                                          @RequestParam(name = "size", defaultValue = "10") int size,
                                                                          @AuthenticationPrincipal CustomUserDetails authUser){
        Page<ItemResponseDto> itemResponseDto = itemService.getMyItems(page, size, authUser);
        return ResponseEntity.ok(ApiResponse.onSuccess(itemResponseDto));
    }

    /**
     * 특정 카테고리에 모든 매물을 조회
     * @param requestDto 페이지 번호와 페이지당 매물 수를 포함하는 요청 객체
     * @param categoryId Category's ID
     * @param authUser 현재 인증된 사용자 정보
     * @return ApiResponse - 메세지, 상태 코드, 특정 카테고리에 속하는 모든 매물 상세 정보를 포함한 응답 객체
     */
    @GetMapping("/items/category/{categoryId}")
    public ResponseEntity<ApiResponse<Page<ItemResponseDto>>> getCategoryItems(@RequestBody @Valid FindItemsInMyAreaRequestDto requestDto,
                                                                               @PathVariable(name = "categoryId")Long categoryId,
                                                                               @AuthenticationPrincipal CustomUserDetails authUser){
        Page<ItemResponseDto> itemResponseDto = itemService.getCategoryItems(requestDto, categoryId, authUser);
        return ResponseEntity.ok(ApiResponse.onSuccess(itemResponseDto));
    }

    /**
     * 우리 동네 매물 조회
     */
    @GetMapping("/items/myAreas")
    public ResponseEntity<ApiResponse<Page<ItemResponseDto>>> getMyAreasItems(@RequestBody @Valid FindItemsInMyAreaRequestDto requestDto,
                                                                              @AuthenticationPrincipal CustomUserDetails authUser) {
        return ResponseEntity.ok(ApiResponse.onSuccess(itemService.findItemsByMyArea(authUser, requestDto)));
    }

    // 우리 동네 인기 매물 조회
    @GetMapping("/items/topItems")
    public ResponseEntity<ApiResponse<List<ItemResponseDto>>> getTopItems(@AuthenticationPrincipal CustomUserDetails authUser){
        return ResponseEntity.ok(ApiResponse.onSuccess(itemService.getTopItems(authUser)));
    }

    // 끌어올리기
    @PutMapping("/items/{itemId}/boost")
    public ResponseEntity<ApiResponse<Void>> boostItem(
            @PathVariable Long itemId,
            @AuthenticationPrincipal CustomUserDetails authUser
    ) {
        itemService.boostItem(itemId, authUser);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }
}
