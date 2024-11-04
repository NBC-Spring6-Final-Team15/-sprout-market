package com.sprarta.sproutmarket.domain.image.itemImage.controller;

import com.sprarta.sproutmarket.domain.common.ApiResponse;
import com.sprarta.sproutmarket.domain.image.itemImage.service.ItemImageService;
import com.sprarta.sproutmarket.domain.item.dto.request.ImageNameRequest;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items/")
public class ItemImageController {
    private final ItemImageService itemImageService;

    // 추가 이미지 업로드
    @PostMapping("/{itemId}/images")
    public ResponseEntity<ApiResponse<String>> itemImageUpload(@PathVariable(name = "itemId") Long itemId,
                                                               @RequestBody ImageNameRequest request,
                                                               @AuthenticationPrincipal CustomUserDetails authUser){
        String image = itemImageService.uploadItemImage(itemId, request.getImageName(), authUser);
        return ResponseEntity.ok(ApiResponse.onSuccess(image));
    }

    @DeleteMapping("/{itemId}/images")
    public ResponseEntity<ApiResponse<String>> itemImageDelete(@PathVariable(name = "itemId") Long itemId,
                                                               @RequestBody ImageNameRequest request,
                                                               @AuthenticationPrincipal CustomUserDetails authUser){
        itemImageService.deleteItemImage(itemId, request.getImageName(), authUser);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }
}
