package com.jesus_dev.arena_hub.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record FacilityScheduleRequestDTO(
        @NotNull(message = "Day is required")
        DayOfWeek day,

        @NotNull(message = "Start time is required")
        LocalTime startTime,

        @NotNull(message = "End time is required")
        LocalTime endTime
) {
    // Validación de que endTime sea posterior a startTime
    @AssertTrue(message = "End time must be after start time")
    public boolean isValidTimeRange() {
        return startTime != null && endTime != null && endTime.isAfter(startTime);
    }
}