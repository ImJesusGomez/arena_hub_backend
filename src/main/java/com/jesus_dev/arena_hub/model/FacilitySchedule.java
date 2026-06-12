package com.jesus_dev.arena_hub.model;

import jakarta.persistence.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity @Table(name = "facility_schedules")
public class FacilitySchedule {

    // Attributes
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING) @Column(name = "day", nullable = false)
    private DayOfWeek day;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "available", nullable = false)
    private boolean available = true;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id")
    private Facility facility;

    // Constructor
    public FacilitySchedule() {}

    public FacilitySchedule(UUID id, DayOfWeek day, LocalDateTime startTime, LocalDateTime endTime, boolean available, Facility facility) {
        this.id = id;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.available = available;
        this.facility = facility;
    }

    // Getters & Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public void setDay(DayOfWeek day) {
        this.day = day;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    // Equals & Hashcode
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FacilitySchedule that)) return false;
        return day == that.day &&
                Objects.equals(startTime, that.startTime) &&
                Objects.equals(endTime, that.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, startTime, endTime);
    }
}
