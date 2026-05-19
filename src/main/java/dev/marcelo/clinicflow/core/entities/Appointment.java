package dev.marcelo.clinicflow.core.entities;

import dev.marcelo.clinicflow.core.enums.AppointmentStatus;

import java.time.LocalDateTime;

public record Appointment(
        Long id,
        Clinic clinic,
        Doctor doctor,
        Patient patient,
        LocalDateTime scheduledAt,
        AppointmentStatus status
) {
}