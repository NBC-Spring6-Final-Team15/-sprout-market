package com.sprarta.sproutmarket.domain.item.controller;

import com.sprarta.sproutmarket.domain.common.ApiResponse;
import com.sprarta.sproutmarket.domain.item.dto.request.ItemContentsUpdateRequest;
import com.sprarta.sproutmarket.domain.item.dto.request.ItemCreateRequest;
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

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<String>> createItem(@RequestBody ItemCreateRequest itemCreateRequest, @AuthenticationPrincipal CustomUserDetails authUser){
        String email = itemService.createItem(itemCreateRequest, authUser);
        return ResponseEntity.ok(ApiResponse.onSuccess(email));
    }

    @PostMapping("/{itemId}/update/sale-status")
    public ResponseEntity<ApiResponse<String>> updateItemSaleStatus(@PathVariable Long itemId, @RequestParam String saleStatus, @AuthenticationPrincipal CustomUserDetails authUser){
        String email = itemService.updateSaleStatus(itemId, saleStatus, authUser);
        return ResponseEntity.ok(ApiResponse.onSuccess(email));
    }

    @PostMapping("/{itemId}/update/contents")
    public ResponseEntity<ApiResponse<String>> updateContents(@PathVariable Long itemId, @RequestBody ItemContentsUpdateRequest request, @AuthenticationPrincipal CustomUserDetails authUser){
        String email = itemService.updateContents(itemId, request, authUser);
        return ResponseEntity.ok(ApiResponse.onSuccess(email));
    }

    @PostMapping("/{itemId}/delete")
    public ResponseEntity<ApiResponse<String>> solfDeleteItem(@PathVariable Long itemId, @AuthenticationPrincipal CustomUserDetails authUser){
        String email = itemService.solfDeleteItem(itemId, authUser);
        return ResponseEntity.ok(ApiResponse.onSuccess(email));
    }

}
