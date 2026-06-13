package com.jesus_dev.arena_hub.dto.response;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

public record FacilityScheduleResponseDTO (
        UUID id,
        DayOfWeek day,
        LocalTime startTime,
        LocalTime endTime,
        boolean available
) {
}
