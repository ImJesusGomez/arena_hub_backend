package com.jesus_dev.arena_hub.mapper;

import com.jesus_dev.arena_hub.dto.request.FacilityCreateRequestDTO;
import com.jesus_dev.arena_hub.dto.response.FacilityResponseDTO;
import com.jesus_dev.arena_hub.model.Facility;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface FacilityMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "facilitySchedules", ignore = true)
    @Mapping(target = "name", qualifiedByName = "trim")
    @Mapping(target = "description", qualifiedByName = "trim")
    @Mapping(target = "locationDetails", qualifiedByName = "trim")
    Facility toEntity(FacilityCreateRequestDTO dto);

    @Mapping(source = "available", target = "available")
    FacilityResponseDTO toFacilityResponseDTO(Facility facility);

    @Named("trim")
    default String strip(String value) {
        return value != null ? value.strip() : null;
    }
}
