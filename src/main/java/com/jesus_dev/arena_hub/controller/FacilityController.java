package com.jesus_dev.arena_hub.controller;

import com.jesus_dev.arena_hub.dto.request.FacilityCreateRequestDTO;
import com.jesus_dev.arena_hub.dto.request.FacilityUpdateRequestDTO;
import com.jesus_dev.arena_hub.dto.response.FacilityResponseDTO;
import com.jesus_dev.arena_hub.model.Facility;
import com.jesus_dev.arena_hub.service.FacilityService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController @RequestMapping("/api/v1/facilities")
public class FacilityController {

    private final FacilityService facilityService;

    public FacilityController(FacilityService facilityService) {
        this.facilityService = facilityService;
    }

    @PreAuthorize("hasAnyRole('DEVELOPER', 'ADMIN')") @PostMapping()
    public ResponseEntity<FacilityResponseDTO> createFacility(@Valid @RequestBody FacilityCreateRequestDTO dto) {
        return new ResponseEntity<>(facilityService.create(dto), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('DEVELOPER', 'ADMIN', 'CLIENT')") @GetMapping("/{id}")
    public ResponseEntity<FacilityResponseDTO> findFacilityById(@PathVariable UUID id) {
        return new ResponseEntity<>(facilityService.findById(id), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('DEVELOPER', 'ADMIN', 'CLIENT')") @GetMapping()
    public ResponseEntity<Page<FacilityResponseDTO>> getFacilities(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(required = false) Integer maxCapacity,
            @RequestParam(required = false) Float minHourlyRate,
            @RequestParam(required = false) Float maxHourlyRate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        return new ResponseEntity<>(facilityService.getFacilities(name, minCapacity, maxCapacity, minHourlyRate, maxHourlyRate, page, size, sortBy, sortDir), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DEVELOPER')") @PatchMapping("/{id}")
    public ResponseEntity<FacilityResponseDTO> updateFacilityById(@PathVariable UUID id, @Valid @RequestBody FacilityUpdateRequestDTO dto) {
        return new ResponseEntity<>(facilityService.update(id, dto), HttpStatus.OK);
    }

}
