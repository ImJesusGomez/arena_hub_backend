package com.jesus_dev.arena_hub.dto.request;

import com.jesus_dev.arena_hub.model.enums.FacilitySpaceType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.Set;

public record FacilityCreateRequestDTO(
        @NotBlank(message = "Name is required")
        @Size(max = 150, message = "Name must not exceed 150 characters")
        String name,

        @NotNull(message = "Space type is required")
        FacilitySpaceType spaceType,

        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Max capacity is required")
        @Positive(message = "Max capacity must be a positive number")
        Integer maxCapacity,

        @NotBlank(message = "Location details are required")
        String locationDetails,

        @NotNull(message = "Min reservation time is required")
        @Positive(message = "Min reservation time must be a positive number")
        Integer minReservationTime,

        @NotNull(message = "Hourly rate is required")
        @PositiveOrZero(message = "Hourly rate must be zero or positive")
        Float hourlyRate,

        @NotNull(message = "Available is required")
        Boolean available,

        @NotEmpty(message = "At least one schedule is required") @Valid
        Set<FacilityScheduleRequestDTO> facilitySchedules
) {}