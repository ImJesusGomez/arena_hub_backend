package com.jesus_dev.arena_hub.service;

import com.jesus_dev.arena_hub.dto.request.ReservationCreateRequestDTO;
import com.jesus_dev.arena_hub.dto.request.ReservationUpdateRequestDTO;
import com.jesus_dev.arena_hub.dto.response.ReservationResponseDTO;
import com.jesus_dev.arena_hub.model.enums.ReservationStatus;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ReservationService {
    ReservationResponseDTO create(ReservationCreateRequestDTO dto);
    ReservationResponseDTO findById(UUID id);
    List<ReservationResponseDTO> findMyReservations();
    Page<ReservationResponseDTO> getReservations(LocalDate startDate, LocalDate endDate, ReservationStatus status, UUID facilityId, int page, int size, String sortBy, String sortDir);
    ReservationResponseDTO update(UUID id, ReservationUpdateRequestDTO dto);
}
