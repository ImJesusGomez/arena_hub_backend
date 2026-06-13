package com.jesus_dev.arena_hub.dto.response;

import com.jesus_dev.arena_hub.model.enums.FacilitySpaceType;

import java.util.Set;
import java.util.UUID;

public record FacilitySummaryResponseDTO(
        UUID id,
        String name,
        FacilitySpaceType spaceType,
        Integer maxCapacity,
        String locationDetails,
        Integer minReservationTime,
        Float hourlyRate
) {
}
