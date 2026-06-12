package com.jesus_dev.arena_hub.dto.request;

import com.jesus_dev.arena_hub.model.enums.FacilitySpaceType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.Set;

public record FacilityUpdateRequestDTO(
        @Size(max = 150, message = "Name must not exceed 150 characters")
        String name,

        FacilitySpaceType spaceType,

        String description,

        @Positive(message = "Max capacity must be a positive number")
        Integer maxCapacity,

        String locationDetails,

        @Positive(message = "Min reservation time must be a positive number")
        Integer minReservationTime,

        @PositiveOrZero(message = "Hourly rate must be zero or positive")
        Float hourlyRate,

        Boolean available,

        @Valid
        Set<FacilityScheduleRequestDTO> facilitySchedules
) {}