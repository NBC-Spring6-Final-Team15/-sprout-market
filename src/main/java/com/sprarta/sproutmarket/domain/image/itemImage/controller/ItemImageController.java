package com.sprarta.sproutmarket.domain.image.itemImage.controller;

import com.sprarta.sproutmarket.domain.common.ApiResponse;
import com.sprarta.sproutmarket.domain.image.dto.ImageResponse;
import com.sprarta.sproutmarket.domain.image.itemImage.service.ItemImageService;
import com.sprarta.sproutmarket.domain.item.dto.request.ImageNameRequest;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items/")
public class ItemImageController {
    private final ItemImageService itemImageService;

    // 추가 이미지 업로드
    @PostMapping("/{itemId}/images")
    public ResponseEntity<ApiResponse<List<ImageResponse>>> itemImageUpload(@PathVariable(name = "itemId") Long itemId,
                                                                      @RequestBody List<ImageNameRequest> requests,
                                                                      @AuthenticationPrincipal CustomUserDetails authUser){
        List<ImageResponse> images = itemImageService.uploadItemImages(itemId, requests, authUser);
        return ResponseEntity.ok(ApiResponse.onSuccess(images));
    }

    @DeleteMapping("/{itemId}/images")
    public ResponseEntity<ApiResponse<String>> itemImageDelete(@PathVariable(name = "itemId") Long itemId,
                                                               @RequestBody ImageNameRequest request,
                                                               @AuthenticationPrincipal CustomUserDetails authUser){
        itemImageService.deleteItemImage(itemId, request, authUser);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }
}
