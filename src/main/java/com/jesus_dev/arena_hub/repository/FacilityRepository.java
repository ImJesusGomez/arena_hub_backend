package com.jesus_dev.arena_hub.repository;

import com.jesus_dev.arena_hub.model.Facility;
import com.jesus_dev.arena_hub.model.FacilitySchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface FacilityRepository extends JpaRepository<Facility, UUID>, JpaSpecificationExecutor<Facility> {
}

