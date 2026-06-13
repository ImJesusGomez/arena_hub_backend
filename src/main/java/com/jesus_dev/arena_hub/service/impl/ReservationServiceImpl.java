package com.jesus_dev.arena_hub.service.impl;

import com.jesus_dev.arena_hub.dto.request.ReservationCreateRequestDTO;
import com.jesus_dev.arena_hub.dto.request.ReservationUpdateRequestDTO;
import com.jesus_dev.arena_hub.dto.response.ReservationResponseDTO;
import com.jesus_dev.arena_hub.exception.ResourceNotFoundException;
import com.jesus_dev.arena_hub.mail.dto.EmailRequestDTO;
import com.jesus_dev.arena_hub.mail.service.EmailService;
import com.jesus_dev.arena_hub.mapper.ReservationMapper;
import com.jesus_dev.arena_hub.model.Facility;
import com.jesus_dev.arena_hub.model.Reservation;
import com.jesus_dev.arena_hub.model.User;
import com.jesus_dev.arena_hub.model.enums.ReservationStatus;
import com.jesus_dev.arena_hub.repository.FacilityRepository;
import com.jesus_dev.arena_hub.repository.ReservationRepository;
import com.jesus_dev.arena_hub.repository.UserRepository;
import com.jesus_dev.arena_hub.service.ReservationService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final FacilityRepository facilityRepository;
    private final ReservationMapper reservationMapper;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public ReservationServiceImpl(ReservationRepository reservationRepository, FacilityRepository facilityRepository, ReservationMapper reservationMapper, UserRepository userRepository, EmailService emailService) {
        this.reservationRepository = reservationRepository;
        this.facilityRepository = facilityRepository;
        this.reservationMapper = reservationMapper;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Override @Transactional
    public ReservationResponseDTO create(ReservationCreateRequestDTO dto) {
        // Creamos la reservación
        Reservation reservationToCreate = new Reservation();

        // Obtenemos el usuario
        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        User user =  userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User Not Found."));

        // Obtenemos el espacio
        Facility facility = facilityRepository.findById(dto.facilityId()).orElseThrow(() -> new ResourceNotFoundException("Facility Not Found."));


        // 1. Verificar que el espacio esté disponible
        if (!facility.isAvailable()) {
            throw new IllegalArgumentException("Facility is not available for reservations.");
        }

        // 2. Verificar que el día tenga horario registrado
        DayOfWeek reservationDay = dto.date().getDayOfWeek();
        boolean dayIsOpen = facility.getFacilitySchedules().stream()
                .anyMatch(s -> s.getDay() == reservationDay
                        && !dto.startTime().isBefore(s.getStartTime())
                        && !dto.endTime().isAfter(s.getEndTime()));

        if (!dayIsOpen) {
            throw new IllegalArgumentException("Facility is not open on " + reservationDay + " at that time.");
        }

        // 3. Verificar que no haya traslape en el espacio ese día
        boolean facilityConflict = reservationRepository.existsConflict(
                facility.getId(), dto.date(), dto.startTime(), dto.endTime()
        );
        if (facilityConflict) {
            throw new IllegalArgumentException("Facility is already reserved during that time slot.");
        }

        // 4. Verificar que el usuario no tenga otra reservación al mismo tiempo
        boolean userConflict = reservationRepository.existsUserConflict(
                user.getId(), dto.date(), dto.startTime(), dto.endTime()
        );
        if (userConflict) {
            throw new IllegalArgumentException("You already have a reservation during that time slot.");
        }

        // 5. Verificar tiempo mínimo de reservación
        long minutes = Duration.between(dto.startTime(), dto.endTime()).toMinutes();
        if (minutes < facility.getMinReservationTime()) {
            throw new IllegalArgumentException(
                    "Minimum reservation time is " + facility.getMinReservationTime() + " minutes."
            );
        }

        // Asignamos sus atributos
        reservationToCreate.setDate(dto.date());
        reservationToCreate.setStatus(ReservationStatus.CONFIRMED);
        reservationToCreate.setCreatedAt(LocalDate.now());
        reservationToCreate.setStartTime(dto.startTime());
        reservationToCreate.setEndTime(dto.endTime());
        reservationToCreate.setObservations("");
        reservationToCreate.setUser(user);
        reservationToCreate.setFacility(facility);

        // Guardamos la reservación
        Reservation reservationCrated = reservationRepository.save(reservationToCreate);

        // Enviamos el correo al usuario
        String bodyText = "Tu reservación para: " + facility.getName() +
                " ha sido registrada con éxito. No olvides asistir el día: " + reservationCrated.getDate() +
                " " + reservationCrated.getStartTime() + " - " + reservationCrated.getEndTime();

        EmailRequestDTO requestDTO = new EmailRequestDTO(
                user.getEmail(),
                "Reservación registrada",
                bodyText,
                null,
                null
        );


        // Retornamos la respuesta
        return reservationMapper.toReservationResponseDTO(reservationCrated);
    }

    @Override @Transactional(readOnly = true)
    public ReservationResponseDTO findById(UUID id) {
        return reservationMapper.toReservationResponseDTO(
                reservationRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Reservation Not Found")));
    }

    @Override @Transactional(readOnly = true)
    public List<ReservationResponseDTO> findMyReservations() {
        // Obtenemos el usuario
        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        User user =  userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User Not Found."));

        // Obtenemos las reservaciones
        List<Reservation> reservations = reservationRepository.findByUser(user);

        // Retornamos la respuesta
        return reservations.stream().map(reservationMapper::toReservationResponseDTO).toList();
    }

    @Override @Transactional(readOnly = true)
    public Page<ReservationResponseDTO> getReservations(LocalDate startDate, LocalDate endDate, ReservationStatus status, UUID facilityId, int page, int size, String sortBy, String sortDir) {
        Specification<Reservation> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), startDate));
            }

            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), endDate));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (facilityId != null) {
                predicates.add(cb.equal(root.get("facility").get("id"), facilityId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return reservationRepository.findAll(spec, pageable).map(reservationMapper::toReservationResponseDTO);
    }

    @Override @Transactional
    public ReservationResponseDTO update(UUID id, ReservationUpdateRequestDTO dto) {
        // 0. Obtenemos la reservación a actualizar
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation Not Found."));

        // 1. No se puede modificar una reservación ya cerrada
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new IllegalArgumentException("Cannot update a cancelled reservation.");
        }
        if (reservation.getStatus() == ReservationStatus.COMPLETED) {
            throw new IllegalArgumentException("Cannot update a completed reservation.");
        }

        // 2. Validar transición de estado
        ReservationStatus newStatus = dto.status();
        ReservationStatus currentStatus = reservation.getStatus();

        if (newStatus == ReservationStatus.COMPLETED && currentStatus != ReservationStatus.CONFIRMED) {
            throw new IllegalArgumentException("Only confirmed reservations can be marked as completed.");
        }

        // 3. Solo ADMIN/DEVELOPER puede marcar como COMPLETED
        if (newStatus == ReservationStatus.COMPLETED) {
            boolean isAdminOrDev = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication())
                    .getAuthorities().stream()
                    .anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_ADMIN") || Objects.equals(a.getAuthority(), "ROLE_DEVELOPER"));

            if (!isAdminOrDev) {
                throw new AccessDeniedException("Only admins can mark a reservation as completed.");
            }
        }

        // 4. Si es CLIENT, solo puede cancelar su propia reservación
        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        boolean isOwner = reservation.getUser().getEmail().equals(email);
        boolean isAdminOrDev = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_ADMIN") || Objects.equals(a.getAuthority(), "ROLE_DEVELOPER"));

        if (!isOwner && !isAdminOrDev) {
            throw new AccessDeniedException("You can only update your own reservations.");
        }

        // Actualizamos
        reservation.setStatus(newStatus);
        if (dto.observation() != null) reservation.setObservations(dto.observation());

        Reservation updated = reservationRepository.save(reservation);

        // Retornamos la respuesta
        return reservationMapper.toReservationResponseDTO(updated);
    }
}
