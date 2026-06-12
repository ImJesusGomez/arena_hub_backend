package com.jesus_dev.arena_hub.service.impl;

import com.jesus_dev.arena_hub.dto.request.FacilityCreateRequestDTO;
import com.jesus_dev.arena_hub.dto.request.FacilityScheduleRequestDTO;
import com.jesus_dev.arena_hub.dto.request.FacilityUpdateRequestDTO;
import com.jesus_dev.arena_hub.dto.response.FacilityResponseDTO;
import com.jesus_dev.arena_hub.exception.ResourceNotFoundException;
import com.jesus_dev.arena_hub.mapper.FacilityMapper;
import com.jesus_dev.arena_hub.mapper.FacilityScheduleMapper;
import com.jesus_dev.arena_hub.model.Facility;
import com.jesus_dev.arena_hub.model.FacilitySchedule;
import com.jesus_dev.arena_hub.repository.FacilityRepository;
import com.jesus_dev.arena_hub.repository.FacilityScheduleRepository;
import com.jesus_dev.arena_hub.service.FacilityService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class FacilityServiceImpl implements FacilityService {

    private final FacilityScheduleRepository facilityScheduleRepository;
    private final FacilityScheduleMapper facilityScheduleMapper;
    private final FacilityRepository facilityRepository;
    private final FacilityMapper facilityMapper;

    public FacilityServiceImpl(FacilityRepository facilityRepository, FacilityMapper facilityMapper, FacilityScheduleMapper facilityScheduleMapper, FacilityScheduleRepository facilityScheduleRepository) {
        this.facilityRepository = facilityRepository;
        this.facilityMapper = facilityMapper;
        this.facilityScheduleMapper = facilityScheduleMapper;
        this.facilityScheduleRepository = facilityScheduleRepository;
    }

    @Override @Transactional
    public FacilityResponseDTO create(FacilityCreateRequestDTO dto) {
        // Creamos el espacio
        Facility facilityToCreate = facilityMapper.toEntity(dto);

        // Creamos los horarios
        Set<FacilitySchedule> facilitySchedules = new HashSet<>();

        // Lógica para guardar los horarios
        for (FacilityScheduleRequestDTO schedule : dto.facilitySchedules()) {
            FacilitySchedule facilitySchedule = facilityScheduleMapper.toEntity(schedule);
            facilitySchedule.setFacility(facilityToCreate);

            boolean added = facilitySchedules.add(facilitySchedule);

            if (!added) {
                throw new IllegalArgumentException(
                        "Duplicate schedule: "
                                + facilitySchedule.getDay() + ". "
                                + facilitySchedule.getStartTime() + " - "
                                + facilitySchedule.getEndTime()
                );
            }
        }

        facilityToCreate.setFacilitySchedules(facilitySchedules);

        // Guardamos el espacio
        Facility facilityCreated = facilityRepository.save(facilityToCreate);

        // Retornamos la respuesta
        return facilityMapper.toFacilityResponseDTO(facilityCreated);
    }

    @Override @Transactional(readOnly = true)
    public FacilityResponseDTO findById(UUID id) {
        return facilityMapper
                .toFacilityResponseDTO(facilityRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Facility not found")));
    }

    @Override @Transactional(readOnly = true)
    public Page<FacilityResponseDTO> getFacilities(String name, Integer minCapacity, Integer maxCapacity, Float minHourlyRate, Float maxHourlyRate, int page, int size, String sortBy, String sortDir) {
        Specification<Facility> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null && !name.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }

            if (minCapacity != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("maxCapacity"), minCapacity));
            }

            if (maxCapacity != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("maxCapacity"), maxCapacity));
            }

            if (minHourlyRate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("hourlyRate"), minHourlyRate));
            }

            if (maxHourlyRate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("hourlyRate"), maxHourlyRate));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return facilityRepository.findAll(spec, pageable).map(facilityMapper::toFacilityResponseDTO);
    }


    @Override @Transactional
    public FacilityResponseDTO update(UUID id, FacilityUpdateRequestDTO dto) {
        // Buscamos el espacio
        Facility facilityToUpdate = facilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facility Not Found"));

        // Actualizamos los horarios
        if (dto.facilitySchedules() != null && !dto.facilitySchedules().isEmpty()) {
            Set<FacilitySchedule> newSchedules = new HashSet<>();

            for (FacilityScheduleRequestDTO schedule : dto.facilitySchedules()) {
                FacilitySchedule facilitySchedule = facilityScheduleMapper.toEntity(schedule);
                facilitySchedule.setFacility(facilityToUpdate);

                boolean added = newSchedules.add(facilitySchedule);

                if (!added) {
                    throw new IllegalArgumentException(
                            "Duplicate schedule: "
                                    + facilitySchedule.getDay() + ". "
                                    + facilitySchedule.getStartTime() + " - "
                                    + facilitySchedule.getEndTime()
                    );
                }
            }

            facilityToUpdate.getFacilitySchedules().clear();   // elimina los viejos
            facilityToUpdate.getFacilitySchedules().addAll(newSchedules); // agrega los nuevos
        }

        // Actualizamos los demás atributos
        if (dto.name() != null) facilityToUpdate.setName(dto.name());
        if (dto.description() != null) facilityToUpdate.setDescription(dto.description());
        if (dto.spaceType() != null) facilityToUpdate.setSpaceType(dto.spaceType());
        if (dto.maxCapacity() != null) facilityToUpdate.setMaxCapacity(dto.maxCapacity());
        if (dto.locationDetails() != null) facilityToUpdate.setLocationDetails(dto.locationDetails());
        if (dto.minReservationTime() != null) facilityToUpdate.setMinReservationTime(dto.minReservationTime());
        if (dto.hourlyRate() != null) facilityToUpdate.setHourlyRate(dto.hourlyRate());
        if (dto.available() != null) facilityToUpdate.setAvailable(dto.available());

        // Actualizamos el espacio
        Facility facilityUpdated = facilityRepository.save(facilityToUpdate);

        // Retornamos la respuesta
        return facilityMapper.toFacilityResponseDTO(facilityUpdated);
    }
}
