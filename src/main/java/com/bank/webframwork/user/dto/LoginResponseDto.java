package com.bank.webframwork.user.dto;

public record LoginResponseDto(
        String accessToken,
        String tokenType,
        long expiresIn
) {
}
