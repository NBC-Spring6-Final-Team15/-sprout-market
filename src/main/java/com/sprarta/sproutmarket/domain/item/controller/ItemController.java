package com.sprarta.sproutmarket.domain.item.controller;

import com.sprarta.sproutmarket.domain.common.dto.response.StatusResponse;
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
    public ResponseEntity<StatusResponse> createItem(@RequestBody ItemCreateRequest itemCreateRequest, @AuthenticationPrincipal CustomUserDetails authUser){
        StatusResponse statusResponse = itemService.createItem(itemCreateRequest, authUser);
        return ResponseEntity.ok(statusResponse);
    }

    @PostMapping("/{itemId}/update/sale-status")
    public ResponseEntity<StatusResponse> updateItemSaleStatus(@PathVariable Long itemId, @RequestParam String saleStatus, @AuthenticationPrincipal CustomUserDetails authUser){
        StatusResponse statusResponse = itemService.updateSaleStatus(itemId, saleStatus, authUser);
        return ResponseEntity.ok(statusResponse);
    }

    @PostMapping("/{itemId}/update/contents")
    public ResponseEntity<StatusResponse> updateContents(@PathVariable Long itemId, @RequestBody ItemContentsUpdateRequest request, @AuthenticationPrincipal CustomUserDetails authUser){
        StatusResponse statusResponse = itemService.updateContents(itemId, request, authUser);
        return ResponseEntity.ok(statusResponse);
    }

    @PostMapping("/{itemId}/delete")
    public ResponseEntity<StatusResponse> solfDeleteItem(@PathVariable Long itemId, @AuthenticationPrincipal CustomUserDetails authUser){
        StatusResponse statusResponse = itemService.solfDeleteItem(itemId, authUser);
        return ResponseEntity.ok(statusResponse);
    }

}
