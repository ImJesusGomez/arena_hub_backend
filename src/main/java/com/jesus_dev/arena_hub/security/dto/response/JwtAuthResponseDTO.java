package com.jesus_dev.arena_hub.security.dto.response;

import com.jesus_dev.arena_hub.dto.response.UserResponseDTO;

public record JwtAuthResponseDTO (
        String accessToken,
        UserResponseDTO user,
        String tokenType
) {
    public JwtAuthResponseDTO(String accessToken, UserResponseDTO user) {
        this(accessToken, user, "Bearer ");
    }
}
