package com.sprarta.sproutmarket.domain.image.controller;

import com.sprarta.sproutmarket.domain.image.service.impl.S3ImageServiceImpl;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class S3ImageController {
    private final S3ImageServiceImpl s3ImageServiceImpl;

    // 이미지 업로드
    @PostMapping("/s3/upload")
    public ResponseEntity<?> s3Upload(@RequestParam(name = "itemId") Long itemId, @RequestPart(value = "image", required = false) MultipartFile image, @AuthenticationPrincipal CustomUserDetails authUser){
        String profileImage = s3ImageServiceImpl.uploadImage(image, itemId, authUser);
        return ResponseEntity.ok(profileImage);
    }

    // 이미지 삭제
    @GetMapping("/s3/delete")
    public ResponseEntity<?> s3delete(@RequestParam(name = "address") String addr){
        s3ImageServiceImpl.deleteImage(addr);
        return ResponseEntity.ok(null);
    }
}
