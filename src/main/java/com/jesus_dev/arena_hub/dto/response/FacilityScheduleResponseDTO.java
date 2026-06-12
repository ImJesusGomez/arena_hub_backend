package com.jesus_dev.arena_hub.dto.response;

import org.springframework.cglib.core.Local;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.UUID;

public record FacilityScheduleResponseDTO (
        UUID id,
        DayOfWeek day,
        LocalDateTime startTime,
        LocalDateTime endTime,
        boolean available
) {
}
