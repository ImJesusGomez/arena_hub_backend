package com.jesus_dev.arena_hub.mapper;

import com.jesus_dev.arena_hub.dto.response.ReservationResponseDTO;
import com.jesus_dev.arena_hub.model.Reservation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReservationMapper {
    ReservationResponseDTO toReservationResponseDTO(Reservation reservation);
}
