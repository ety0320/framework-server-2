package com.bank.webframwork.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Locale;

public record SignUpRequestDto(
        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "올바른 이메일을 입력해주세요.")
            @Size(max = 100, message = "이메일은 100자를 넘길 수 없습니다.")
        String email,

        @NotBlank(message = "비밀번호를 입력해주세요.")
            @Size(min = 8, max = 20, message = "비밀번호는 20자를 넘길 수 없습니다.")
        String password,

        @NotBlank(message = "닉네임을 입력해주세요.")
            @Size(min = 2, max = 10, message = "닉네임은 10자를 넘길 수 없습니다.")
        String nickname
) {
    public SignUpRequestDto {
        email = email == null ? null : email.toLowerCase(Locale.ROOT);
        nickname = nickname == null ? null : nickname.trim();
    }
}
