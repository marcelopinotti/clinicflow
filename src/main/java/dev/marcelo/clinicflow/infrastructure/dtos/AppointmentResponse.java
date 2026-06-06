package dev.marcelo.clinicflow.infrastructure.dtos;

import dev.marcelo.clinicflow.core.enums.AppointmentStatus;

import java.time.LocalDateTime;

public record AppointmentResponse(
        Long id,
        Long clinicId,
        Long doctorId,
        Long patientId,
        LocalDateTime scheduledAt,
        AppointmentStatus status
) {
}

