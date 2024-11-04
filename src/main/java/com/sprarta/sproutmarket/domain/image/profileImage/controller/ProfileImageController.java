package com.sprarta.sproutmarket.domain.image.profileImage.controller;

import com.sprarta.sproutmarket.domain.common.ApiResponse;
import com.sprarta.sproutmarket.domain.image.profileImage.dto.ProfileImageResponse;
import com.sprarta.sproutmarket.domain.image.profileImage.service.ProfileImageService;
import com.sprarta.sproutmarket.domain.item.dto.request.ImageNameRequest;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/profile")
public class ProfileImageController {
    private final ProfileImageService profileImageService;

    // 프로필 이미지 업로드
    @PostMapping("/image")
    public ResponseEntity<ApiResponse<ProfileImageResponse>> profileImageUpload(@RequestBody ImageNameRequest request,
                                                                                @AuthenticationPrincipal CustomUserDetails authUser){
        ProfileImageResponse profileImageResponse = profileImageService.uploadProfileImage(request, authUser);
        return ResponseEntity.ok(ApiResponse.onSuccess(profileImageResponse));
    }

    @DeleteMapping("/image")
    public ResponseEntity<ApiResponse<String>> profileImageDelete(@RequestBody ImageNameRequest request,
                                                               @AuthenticationPrincipal CustomUserDetails authUser){
        profileImageService.deleteProfileImage(request, authUser);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }
}
