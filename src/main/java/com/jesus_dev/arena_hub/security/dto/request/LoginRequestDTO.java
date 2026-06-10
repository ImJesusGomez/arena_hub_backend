package com.jesus_dev.arena_hub.security.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO (
        @NotBlank(message = "Email is required.")
        String email,
        @NotBlank(message = "Password is required.")
        String password
) {
}
