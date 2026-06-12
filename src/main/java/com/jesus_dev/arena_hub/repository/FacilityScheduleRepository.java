package com.jesus_dev.arena_hub.repository;

import com.jesus_dev.arena_hub.model.FacilitySchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FacilityScheduleRepository extends JpaRepository<FacilitySchedule, UUID> {
}
