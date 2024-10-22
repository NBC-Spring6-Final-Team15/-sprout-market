package com.sprarta.sproutmarket.domain.user.controller;

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
    public ResponseEntity<UserResponse> getUser(@PathVariable long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @PutMapping
    public void changePassword(
            @AuthenticationPrincipal CustomUserDetails authUser,
            @RequestBody @Valid UserChangePasswordRequest userChangePasswordRequest) {
        userService.changePassword(authUser, userChangePasswordRequest);
    }

    @DeleteMapping
    public void deleteUser(
            @AuthenticationPrincipal CustomUserDetails authUser,
            @RequestBody @Valid UserDeleteRequest userDeleteRequest) {
        userService.deleteUser(authUser, userDeleteRequest);
    }
}
