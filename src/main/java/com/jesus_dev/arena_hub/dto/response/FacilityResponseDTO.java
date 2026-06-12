package com.jesus_dev.arena_hub.dto.response;

import com.jesus_dev.arena_hub.model.enums.FacilitySpaceType;

import java.util.Set;
import java.util.UUID;

public record FacilityResponseDTO (
        UUID id,
        String name,
        FacilitySpaceType spaceType,
        String description,
        Integer maxCapacity,
        String locationDetails,
        Integer minReservationTime,
        Float hourlyRate,
        boolean available,
        Set<FacilityScheduleResponseDTO> facilitySchedules
) {
}
