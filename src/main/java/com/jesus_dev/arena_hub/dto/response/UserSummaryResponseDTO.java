package com.jesus_dev.arena_hub.dto.response;

import java.util.Set;
import java.util.UUID;

public record UserSummaryResponseDTO(
        UUID id,
        String firstName,
        String lastName
) {
}
