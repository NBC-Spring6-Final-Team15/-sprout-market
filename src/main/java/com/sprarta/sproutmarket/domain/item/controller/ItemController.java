package com.sprarta.sproutmarket.domain.item.controller;

import com.sprarta.sproutmarket.domain.common.ApiResponse;
import com.sprarta.sproutmarket.domain.item.dto.request.FindItemsInMyAreaRequestDto;
import com.sprarta.sproutmarket.domain.item.dto.request.ItemContentsUpdateRequest;
import com.sprarta.sproutmarket.domain.item.dto.request.ItemCreateRequest;
import com.sprarta.sproutmarket.domain.item.dto.request.ItemSearchRequest;
import com.sprarta.sproutmarket.domain.item.dto.response.ItemResponse;
import com.sprarta.sproutmarket.domain.item.dto.response.ItemResponseDto;
import com.sprarta.sproutmarket.domain.item.service.ItemService;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
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
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<Page<ItemResponseDto>>> searchItems(@RequestParam(name = "page", defaultValue = "1") int page,
                                                                               @RequestParam(name = "size", defaultValue = "10") int size,
                                                                               @RequestBody ItemSearchRequest itemSearchRequest,
                                                                               @AuthenticationPrincipal CustomUserDetails authUser){
        Page<ItemResponseDto> itemResponseDto = itemService.searchItems(page, size, itemSearchRequest, authUser);
        return ResponseEntity.ok(ApiResponse.onSuccess(itemResponseDto));
    }

            /**
     * 로그인한 사용자가 중고 물품을 등록하는 로직
     * @param itemCreateRequest 매물 세부 정보를 포함한 요청 객체(제목, 설명, 가격, 카테고리id)
     * @param authUser 매물 등록을 요청한 사용자
     * @return ApiResponse - 생성된 아이템에 대한 메세지, 상태 코드를 포함한 응답 객체
     */
    @PostMapping
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
    @PutMapping("/{itemId}/sale-status")
    public ResponseEntity<ApiResponse<ItemResponse>> updateItemSaleStatus(@PathVariable(name = "itemId") Long itemId, @RequestParam String saleStatus, @AuthenticationPrincipal CustomUserDetails authUser){
        ItemResponse itemResponse = itemService.updateSaleStatus(itemId, saleStatus, authUser);
        return ResponseEntity.ok(ApiResponse.onSuccess(itemResponse));
    }

    /**
     * 매물의 내용(제목, 설명, 가격, 이미지URL)을 수정하는 로직
     * @param itemId Item's ID
     * @param itemContentsUpdateRequest 매물 수정 정보를 포함한 요청 객체(제목, 내용, 가격, 이미지URL)
     * @param authUser 매물 내용 수정을 요청한 사용자
     * @return ApiResponse - 내용이 수정된 아이템에 대한 정보, 메세지, 상태 코드를 포함한 응답 객체
     */
    @PutMapping("/{itemId}/contents")
    public ResponseEntity<ApiResponse<ItemResponse>> updateContent(@PathVariable(name = "itemId") Long itemId, @RequestBody ItemContentsUpdateRequest itemContentsUpdateRequest, @AuthenticationPrincipal CustomUserDetails authUser){
        ItemResponse itemResponse = itemService.updateContents(itemId, itemContentsUpdateRequest, authUser);
        return ResponseEntity.ok(ApiResponse.onSuccess(itemResponse));
    }

    /**
     * 매물에 이미지를 추가하는 로직
     * @param itemId Item's ID
     * @param authUser 매물 내용 수정을 요청한 사용자
     * @param image 업로드할 이미지 파일. 사용자가 업로드한 파일을 MultipartFile 형식으로 받음
     * @return ApiResponse - 이미지가 수정된 아이템에 대한 정보, 메세지, 상태 코드를 포함한 응답 객체
     */
    @PutMapping("/{itemId}/image")
    public ResponseEntity<ApiResponse<ItemResponse>> addItemImage(@PathVariable(name = "itemId") Long itemId, @AuthenticationPrincipal CustomUserDetails authUser, @RequestPart(value = "image", required = false, name = "image") MultipartFile image){
        ItemResponse itemResponse = itemService.addImage(itemId, authUser, image);
        return ResponseEntity.ok(ApiResponse.onSuccess(itemResponse));
    }

    /**
     * 매물에 저장된 이미지를 삭제하는 로직
     * @param itemId Item's ID
     * @param authUser 매물 내용 수정을 요청한 사용자
     * @param imageId 삭제할 이미지의 ID
     * @return ApiResponse - 특정 이미지가 삭제된 아이템에 대한 정보, 메세지, 상태 코드를 포함한 응답 객체
     */
    @DeleteMapping("/{itemId}/image")
    public ResponseEntity<ApiResponse<ItemResponse>> removeItemImage(@PathVariable(name = "itemId") Long itemId, @AuthenticationPrincipal CustomUserDetails authUser, @RequestParam(name = "imageId") Long imageId){
        ItemResponse itemResponse = itemService.deleteImage(itemId, authUser, imageId);
        return ResponseEntity.ok(ApiResponse.onSuccess(itemResponse));
    }

    /**
     * 자신이 등록한 매물을 (논리적)삭제하는 로직
     * @param itemId Item's ID
     * @param authUser 매물 내용 수정을 요청한 사용자
     * @return ApiResponse - 메세지, 상태 코드, 삭제한 아이템에 대한 정보를 포함한 응답 객체
     */
    @DeleteMapping("/{itemId}")
    public ResponseEntity<ApiResponse<ItemResponse>> softRemoveItem(@PathVariable(name = "itemId") Long itemId, @AuthenticationPrincipal CustomUserDetails authUser){
        ItemResponse itemResponse = itemService.softDeleteItem(itemId, authUser);
        return ResponseEntity.ok(ApiResponse.onSuccess(itemResponse));
    }

    /**
     * 관리자가 신고된 매물을 (논리적)삭제하는 로직
     * @param itemId Item's ID
     * @param authUser 매물 내용 수정을 요청한 사용자
     * @return ApiResponse - 메세지, 상태 코드, 삭제된 아이템에 대한 정보를 포함한 응답 객체
     */
    @DeleteMapping("/{itemId}/report")
    public ResponseEntity<ApiResponse<ItemResponse>> softRemoveReportedItem(@PathVariable(name = "itemId") Long itemId, @AuthenticationPrincipal CustomUserDetails authUser){
        ItemResponse itemResponse = itemService.softDeleteReportedItem(itemId, authUser);
        return ResponseEntity.ok(ApiResponse.onSuccess(itemResponse));
    }

    /**
     * 로그인 한 사용자가 특정 매물을 상세조회하는 로직
     * @param itemId Item's ID
     * @return ApiResponse - 메세지, 상태 코드, 아이템의 상세 정보를 포함한 응답 객체
     */
    @GetMapping("/{itemId}")
    public ResponseEntity<ApiResponse<ItemResponseDto>> findItem(@PathVariable(name = "itemId") Long itemId){
        ItemResponseDto itemResponseDto = itemService.getItem(itemId);
        return ResponseEntity.ok(ApiResponse.onSuccess(itemResponseDto));
    }

    /**
     * 현재 인증된 사용자의 모든 매물을 조회하는 로직
     * @param page 페이지 번호(1부터 시작)
     * @param size 페이지당 카드 수
     * @param authUser 현재 인증된 사용자 정보
     * @return ApiResponse - 메세지, 상태 코드, 로그인한 사용자의 모든 매물 상세 정보를 포함한 응답 객체
     */
    @GetMapping("/mine")
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
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<Page<ItemResponseDto>>> getCategoryItems(@RequestBody @Valid FindItemsInMyAreaRequestDto requestDto,
                                                                               @PathVariable(name = "categoryId")Long categoryId,
                                                                               @AuthenticationPrincipal CustomUserDetails authUser){
        Page<ItemResponseDto> itemResponseDto = itemService.getCategoryItems(requestDto, categoryId, authUser);
        return ResponseEntity.ok(ApiResponse.onSuccess(itemResponseDto));
    }

    /**
     * 우리 동네 매물 조회
     */
    @GetMapping("/myAreas")
    public ResponseEntity<ApiResponse<Page<ItemResponseDto>>> getMyAreasItems(@RequestBody @Valid FindItemsInMyAreaRequestDto requestDto,
                                                                              @AuthenticationPrincipal CustomUserDetails authUser) {
        return ResponseEntity.ok(ApiResponse.onSuccess(itemService.findItemsByMyArea(authUser, requestDto)));
    }

    // 우리 동네 인기 매물 조회
    @GetMapping("/topItems")
    public ResponseEntity<ApiResponse<List<ItemResponseDto>>> getTopItems(@AuthenticationPrincipal CustomUserDetails authUser){
        return ResponseEntity.ok(ApiResponse.onSuccess(itemService.getTopItems(authUser)));
    }

}
