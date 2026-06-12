package com.jesus_dev.arena_hub.model;

import com.jesus_dev.arena_hub.model.enums.FacilitySpaceType;
import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "facilities")
public class Facility {
    // Attributes
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", length = 150, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING) @Column(name = "space_type", nullable = false)
    private FacilitySpaceType spaceType;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "max_capacity", nullable = false)
    private Integer maxCapacity;

    @Column(name = "location_details", nullable = false)
    private String locationDetails;

    @Column(name = "min_reservation_time", nullable = false)
    private Integer minReservationTime;

    @Column(name = "hourly_rate", nullable = false)
    private Float hourlyRate;

    @Column(name = "available", nullable = false)
    private Boolean available = true;

    // Relationships
    @OneToMany(mappedBy = "facility", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FacilitySchedule> facilitySchedules = new HashSet<>();

    // Constructor
    public Facility() {
    }

    public Facility(UUID id, String name, FacilitySpaceType spaceType, String description, Integer maxCapacity, String locationDetails, Integer minReservationTime, Float hourlyRate, Boolean available, Set<FacilitySchedule> facilitySchedules) {
        this.id = id;
        this.name = name;
        this.spaceType = spaceType;
        this.description = description;
        this.maxCapacity = maxCapacity;
        this.locationDetails = locationDetails;
        this.minReservationTime = minReservationTime;
        this.hourlyRate = hourlyRate;
        this.available = available;
        this.facilitySchedules = facilitySchedules;
    }

    // Getters & Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FacilitySpaceType getSpaceType() {
        return spaceType;
    }

    public void setSpaceType(FacilitySpaceType spaceType) {
        this.spaceType = spaceType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public String getLocationDetails() {
        return locationDetails;
    }

    public void setLocationDetails(String locationDetails) {
        this.locationDetails = locationDetails;
    }

    public Integer getMinReservationTime() {
        return minReservationTime;
    }

    public void setMinReservationTime(Integer minReservationTime) {
        this.minReservationTime = minReservationTime;
    }

    public Float getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(Float hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Set<FacilitySchedule> getFacilitySchedules() {
        return facilitySchedules;
    }

    public void setFacilitySchedules(Set<FacilitySchedule> facilitySchedules) {
        this.facilitySchedules = facilitySchedules;
    }
}