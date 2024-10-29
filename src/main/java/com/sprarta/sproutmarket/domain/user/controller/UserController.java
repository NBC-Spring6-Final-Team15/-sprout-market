package com.sprarta.sproutmarket.domain.user.controller;

import com.sprarta.sproutmarket.domain.common.ApiResponse;
import com.sprarta.sproutmarket.domain.user.dto.request.UserAddressUpdateRequest;
import com.sprarta.sproutmarket.domain.user.dto.request.UserChangePasswordRequest;
import com.sprarta.sproutmarket.domain.user.dto.request.UserDeleteRequest;
import com.sprarta.sproutmarket.domain.user.dto.response.UserResponse;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import com.sprarta.sproutmarket.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ApiResponse<String>> changePassword(
            @AuthenticationPrincipal CustomUserDetails authUser,
            @RequestBody @Valid UserChangePasswordRequest userChangePasswordRequest) {
        userService.changePassword(authUser, userChangePasswordRequest);
        return ResponseEntity.ok(ApiResponse.onSuccess("비밀번호가 설공적으로 변경되었습니다."));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<String>> deleteUser(
            @AuthenticationPrincipal CustomUserDetails authUser,
            @RequestBody @Valid UserDeleteRequest userDeleteRequest) {
        userService.deleteUser(authUser, userDeleteRequest);
        return ResponseEntity.ok(ApiResponse.onSuccess("탈퇴에 성공하였습니다."));
    }

    @PatchMapping()
    public ResponseEntity<ApiResponse<String>> updateUserAddress(
            @RequestBody UserAddressUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails authUser
    ) {
        userService.updateUserAddress(authUser.getId(), request.getLongitude(), request.getLatitude());
        return ResponseEntity.ok(ApiResponse.onSuccess("주소가 성공적으로 업데이트되었습니다."));
    }
}
