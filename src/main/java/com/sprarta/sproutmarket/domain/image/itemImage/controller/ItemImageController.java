package com.sprarta.sproutmarket.domain.image.itemImage.controller;

import com.sprarta.sproutmarket.domain.common.ApiResponse;
import com.sprarta.sproutmarket.domain.image.itemImage.service.ItemImageService;
import com.sprarta.sproutmarket.domain.image.s3Image.service.S3ImageService;
import com.sprarta.sproutmarket.domain.item.dto.request.ImageNameRequest;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemImageController {
    private final ItemImageService itemImageService;
    private final S3ImageService s3ImageService;

    // 여러 이미지 업로드
    @PostMapping("/images")
    public ResponseEntity<ApiResponse<List<String>>> itemImageUpload(
            @RequestParam("images") List<MultipartFile> images,
            @AuthenticationPrincipal CustomUserDetails authUser) {

        List<CompletableFuture<String>> futures = images.stream()
                .map(image -> s3ImageService.uploadImageAsync(image, authUser))
                .toList();

        List<String> imageUrls = futures.stream().map(CompletableFuture::join).toList();
        return ResponseEntity.ok(ApiResponse.onSuccess(imageUrls));
    }

    @DeleteMapping("/{itemId}/images")
    public ResponseEntity<ApiResponse<String>> itemImageDelete(@PathVariable(name = "itemId") Long itemId,
                                                               @RequestBody ImageNameRequest request,
                                                               @AuthenticationPrincipal CustomUserDetails authUser){
        itemImageService.deleteItemImage(itemId, request, authUser);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }
}
