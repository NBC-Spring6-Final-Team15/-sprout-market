package com.sprarta.sproutmarket.domain.image.controller;

import com.sprarta.sproutmarket.domain.image.service.S3ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class S3ImageController {
    private final S3ImageService s3ImageService;

    // 이미지 업로드
    @PostMapping("/s3/upload")
    public ResponseEntity<?> s3Upload(@RequestPart(value = "image", required = false) MultipartFile image){
        String profileImage = s3ImageService.upload(image);
        return ResponseEntity.ok(profileImage);
    }

    // 이미지 삭제
    @GetMapping("/s3/delete")
    public ResponseEntity<?> s3delete(@RequestParam(name = "address") String addr){
        s3ImageService.deleteImageFromS3(addr);
        return ResponseEntity.ok(null);
    }
}
