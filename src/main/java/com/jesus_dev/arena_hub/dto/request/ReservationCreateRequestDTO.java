package com.jesus_dev.arena_hub.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record ReservationCreateRequestDTO (
        @NotNull(message = "Reservation date is required.")
        @FutureOrPresent(message = "Reservation date must be today or in the future.")
        LocalDate date,

        @NotNull(message = "Start time is required.")
        LocalTime startTime,

        @NotNull(message = "End time is required.")
        LocalTime endTime,

        @NotNull(message = "Facility ID is required.")
        UUID facilityId
) {
}