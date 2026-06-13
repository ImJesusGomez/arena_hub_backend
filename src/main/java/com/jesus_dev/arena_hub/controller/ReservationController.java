package com.jesus_dev.arena_hub.controller;

import com.jesus_dev.arena_hub.dto.request.ReservationCreateRequestDTO;
import com.jesus_dev.arena_hub.dto.request.ReservationUpdateRequestDTO;
import com.jesus_dev.arena_hub.dto.response.ReservationResponseDTO;
import com.jesus_dev.arena_hub.model.enums.ReservationStatus;
import com.jesus_dev.arena_hub.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController @RequestMapping("/api/v1/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DEVELOPER', 'CLIENT')") @PostMapping()
    public ResponseEntity<ReservationResponseDTO> createReservation(@Valid @RequestBody ReservationCreateRequestDTO dto) {
        return new ResponseEntity<>(reservationService.create(dto), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DEVELOPER', 'CLIENT')") @GetMapping("/{id}")
    public ResponseEntity<ReservationResponseDTO> findReservationById(@PathVariable UUID id) {
        return new ResponseEntity<>(reservationService.findById(id), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DEVELOPER', 'CLIENT')") @GetMapping("/my-reservations")
    public ResponseEntity<List<ReservationResponseDTO>> findMyReservations() {
        return new ResponseEntity<>(reservationService.findMyReservations(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DEVELOPER')") @GetMapping()
    public ResponseEntity<Page<ReservationResponseDTO>> getReservations(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) ReservationStatus status,
            @RequestParam(required = false) UUID facilityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        return new ResponseEntity<>(
                reservationService.getReservations(startDate, endDate, status, facilityId, page, size, sortBy, sortDir),
                HttpStatus.OK
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DEVELOPER', 'CLIENT')") @PatchMapping("/{id}")
    public ResponseEntity<ReservationResponseDTO> updateReservation(
            @PathVariable UUID id,
            @Valid @RequestBody ReservationUpdateRequestDTO dto
    ) {
        return new ResponseEntity<>(reservationService.update(id, dto), HttpStatus.OK);
    }
}
