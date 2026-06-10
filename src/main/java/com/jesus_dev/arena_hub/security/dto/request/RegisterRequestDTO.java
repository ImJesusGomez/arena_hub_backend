package com.jesus_dev.arena_hub.security.dto.request;

import com.jesus_dev.arena_hub.model.enums.RoleName;
import jakarta.validation.constraints.*;

import java.util.Set;

public record RegisterRequestDTO (
        @NotBlank(message = "First Name is required.")
        @Size(min = 1, max = 60, message = "First Name must be between 1 and 60 characters.")
        String firstName,

        @NotBlank(message = "Last Name is required.")
        @Size(min = 1, max = 60, message = "Last Name must be between 1 and 60 characters.")
        String lastName,

        @Email(message = "Must be a valid email.")
        String email,

        @NotBlank(message = "Password is required.")
        @Size(min = 8, message = "Password must have 8 or more characters.")
        String password,

        @NotEmpty(message = "Roles are required.")
        Set<@NotNull(message = "Role Name is required.") RoleName> roles
) {
}
