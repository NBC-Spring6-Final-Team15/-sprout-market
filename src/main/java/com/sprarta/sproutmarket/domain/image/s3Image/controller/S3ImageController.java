package com.sprarta.sproutmarket.domain.image.s3Image.controller;

import com.sprarta.sproutmarket.domain.common.ApiResponse;
import com.sprarta.sproutmarket.domain.image.s3Image.service.S3ImageService;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class S3ImageController {
    private final S3ImageService s3ImageService;

    // 이미지 업로드(매물 추가 되기 전)
    @PostMapping("/s3/upload")
    public ResponseEntity<ApiResponse<String>> s3Upload(@RequestPart(value = "image", required = false) MultipartFile image, @AuthenticationPrincipal CustomUserDetails authUser){
        String imageName = s3ImageService.uploadImage(image, authUser);
        return ResponseEntity.ok(ApiResponse.onSuccess(imageName));
    }
}
