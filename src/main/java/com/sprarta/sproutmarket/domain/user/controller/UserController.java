package com.sprarta.sproutmarket.domain.user.controller;

import com.sprarta.sproutmarket.domain.common.ApiResponse;
import com.sprarta.sproutmarket.domain.user.dto.request.UserAddressUpdateRequest;
import com.sprarta.sproutmarket.domain.user.dto.request.UserChangePasswordRequest;
import com.sprarta.sproutmarket.domain.user.dto.request.UserDeleteRequest;
import com.sprarta.sproutmarket.domain.user.dto.response.UserAdminResponse;
import com.sprarta.sproutmarket.domain.user.dto.response.UserResponse;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import com.sprarta.sproutmarket.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable long userId) {
        return ResponseEntity.ok(ApiResponse.onSuccess(userService.getUser(userId)));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal CustomUserDetails authUser,
            @RequestBody @Valid UserChangePasswordRequest userChangePasswordRequest) {
        userService.changePassword(authUser, userChangePasswordRequest);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @AuthenticationPrincipal CustomUserDetails authUser,
            @RequestBody @Valid UserDeleteRequest userDeleteRequest) {
        userService.deleteUser(authUser, userDeleteRequest);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @PatchMapping()
    public ResponseEntity<ApiResponse<Void>> updateUserAddress(
            @RequestBody UserAddressUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails authUser
    ) {
        userService.updateUserAddress(authUser.getId(), request.getLongitude(), request.getLatitude());
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @PutMapping("/profile-image")
    public ResponseEntity<ApiResponse<String>> updateProfileImage(
            @AuthenticationPrincipal CustomUserDetails authUser,
            @RequestPart(value = "image", required = true) MultipartFile image) {
        String profileImageUrl = userService.updateProfileImage(authUser, image);
        return ResponseEntity.ok(ApiResponse.onSuccess(profileImageUrl));
    }

    @DeleteMapping("/profile-image")
    public ResponseEntity<ApiResponse<Void>> deleteProfileImage(@AuthenticationPrincipal CustomUserDetails authUser) {
        userService.deleteProfileImage(authUser);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @PatchMapping("/admin/deleted/{userId}")
    public ResponseEntity<ApiResponse<Void>> activateUser(@PathVariable long userId) {
        userService.activateUser(userId);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<List<UserAdminResponse>>> getUsers() {
        return ResponseEntity.ok(ApiResponse.onSuccess(userService.getAllUsers()));
    }
}
