package com.jesus_dev.arena_hub.dto.request;

import com.jesus_dev.arena_hub.model.enums.ReservationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReservationUpdateRequestDTO (
        @NotNull(message = "Reservation status is required.")
        ReservationStatus status,

        @Size(max = 200, message = "Observation cannot exceed 500 characters.")
        String observation
) {
}