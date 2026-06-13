package com.jesus_dev.arena_hub.repository;

import com.jesus_dev.arena_hub.model.Reservation;
import com.jesus_dev.arena_hub.model.User;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID>, JpaSpecificationExecutor<Reservation> {
    @Query("""
    SELECT COUNT(r) > 0 FROM Reservation r
    WHERE r.facility.id = :facilityId
      AND r.date = :date
      AND r.status != 'CANCELLED'
      AND r.startTime < :endTime
      AND r.endTime > :startTime
    """)
    boolean existsConflict(
            @Param("facilityId") UUID facilityId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

    // Traslape del usuario
    @Query("""
    SELECT COUNT(r) > 0 FROM Reservation r
    WHERE r.user.id = :userId
      AND r.date = :date
      AND r.status != 'CANCELLED'
      AND r.startTime < :endTime
      AND r.endTime > :startTime
    """)
    boolean existsUserConflict(
            @Param("userId") UUID userId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );


    List<Reservation> findByUser(User user);
}
