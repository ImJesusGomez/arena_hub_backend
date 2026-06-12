package com.jesus_dev.arena_hub.mapper;

import com.jesus_dev.arena_hub.dto.request.FacilityScheduleRequestDTO;
import com.jesus_dev.arena_hub.dto.response.FacilityScheduleResponseDTO;
import com.jesus_dev.arena_hub.model.FacilitySchedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FacilityScheduleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "facility", ignore = true)
    FacilitySchedule toEntity(FacilityScheduleRequestDTO dto);

    @Mapping(source = "available", target = "available")
    FacilityScheduleResponseDTO toFacilityScheduleResponseDTO(FacilitySchedule facilitySchedule);
}
