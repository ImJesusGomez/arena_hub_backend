package com.jesus_dev.arena_hub.model.enums;

public enum ReservationStatus {
    CONFIRMED,      // El cliente es esperado.
    CANCELLED,      // El cliente o el sistema la cancelaron
    COMPLETED,      // El cliente ya asistió y el servicio terminó (Historial).
    NO_SHOW      // El cliente no se presentó
}
