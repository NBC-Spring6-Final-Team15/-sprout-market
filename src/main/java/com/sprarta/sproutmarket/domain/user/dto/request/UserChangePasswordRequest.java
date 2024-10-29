package com.sprarta.sproutmarket.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserChangePasswordRequest {
    @NotBlank(message = "기존 비밀번호는 필수 입력 항목입니다.")
    private String oldPassword;

    @NotBlank(message = "새 비밀번호는 필수 입력 항목입니다.")
    @Size(min = 8, message = "새 비밀번호는 8자 이상이어야 합니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[!@#$%^&*]).{8,20}$",
            message = "비밀번호는 대소문자 포함 영문 + 숫자 + 특수문자 1개 이상 최소 8글자 입니다.")
    private String newPassword;
}
