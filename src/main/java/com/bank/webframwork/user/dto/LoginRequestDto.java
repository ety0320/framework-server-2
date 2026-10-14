package com.bank.webframwork.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Locale;

public record LoginRequestDto(
        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "메세지를 입력해주세요.")
        @Size(max = 20)
        String email,

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Size(max = 20)
        String password
) {
    public LoginRequestDto {
        email = email == null ? null : email.trim()
        .toLowerCase(Locale.ROOT);
    }
}
