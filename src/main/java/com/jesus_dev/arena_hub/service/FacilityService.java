package com.jesus_dev.arena_hub.service;

import com.jesus_dev.arena_hub.dto.request.FacilityCreateRequestDTO;
import com.jesus_dev.arena_hub.dto.request.FacilityUpdateRequestDTO;
import com.jesus_dev.arena_hub.dto.response.FacilityResponseDTO;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface FacilityService {
    FacilityResponseDTO create(FacilityCreateRequestDTO dto);
    FacilityResponseDTO findById(UUID id);
    Page<FacilityResponseDTO> getFacilities(String name, Integer minCapacity, Integer maxCapacity, Float minHourlyRate, Float maxHourlyRate, int page, int size, String sortBy, String sortDir);
    FacilityResponseDTO update(UUID id, FacilityUpdateRequestDTO dto);
}
