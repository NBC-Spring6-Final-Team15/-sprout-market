package com.sprarta.sproutmarket.domain.user.service;

import com.sprarta.sproutmarket.domain.user.dto.request.UserChangePasswordRequest;
import com.sprarta.sproutmarket.domain.user.dto.request.UserDeleteRequest;
import com.sprarta.sproutmarket.domain.user.dto.response.UserResponse;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import com.sprarta.sproutmarket.domain.user.entity.User;
import com.sprarta.sproutmarket.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public UserResponse getUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        return new UserResponse(user.getId(), user.getEmail());
    }

    @Transactional
    public void changePassword(CustomUserDetails authUser, UserChangePasswordRequest userChangePasswordRequest) {

        // 1. 유저 존재 여부 확인
        User user = userRepository.findById(authUser.getId()).orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // 2. 기존 비밀번호가 맞는지 확인
        if (!passwordEncoder.matches(userChangePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }

        // 3. 새 비밀번호가 기존 비밀번호와 동일한지 확인 (암호화하지 않은 상태로 비교)
        if (userChangePasswordRequest.getOldPassword().equals(userChangePasswordRequest.getNewPassword())) {
            throw new IllegalArgumentException("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.");
        }

        // 4. 새 비밀번호가 유효한지 확인
        validateNewPassword(userChangePasswordRequest);

        // 5. 새 비밀번호 암호화 후 저장
        user.changePassword(passwordEncoder.encode(userChangePasswordRequest.getNewPassword()));
    }

    private static void validateNewPassword(UserChangePasswordRequest userChangePasswordRequest) {
        if (userChangePasswordRequest.getNewPassword().length() < 8 ||
                !userChangePasswordRequest.getNewPassword().matches(".*\\d.*") ||
                !userChangePasswordRequest.getNewPassword().matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.");
        }
    }

    @Transactional
    public void deleteUser(CustomUserDetails authUser, UserDeleteRequest userDeleteRequest) {

        // 1. 유저 존재 여부 확인
        User user = userRepository.findById(authUser.getId()).orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // 1. 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(userDeleteRequest.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }

        // 2. 유저 비활성화 및 삭제
        user.deactivate();
        userRepository.delete(user);
    }
}

