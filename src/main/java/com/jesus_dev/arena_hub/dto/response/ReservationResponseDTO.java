package com.jesus_dev.arena_hub.dto.response;

import com.jesus_dev.arena_hub.model.enums.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record ReservationResponseDTO (
        UUID id,
        LocalDate date,
        ReservationStatus status,
        LocalDate createdAt,
        LocalTime startTime,
        LocalTime endTime,
        String observations,
        UserSummaryResponseDTO user,
        FacilitySummaryResponseDTO facility
) {
}
