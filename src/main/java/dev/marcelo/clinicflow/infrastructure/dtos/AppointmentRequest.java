package dev.marcelo.clinicflow.infrastructure.dtos;

import java.time.LocalDateTime;

public record AppointmentRequest(
        Long id,
        Long clinicId,
        Long doctorId,
        Long patientId,
        LocalDateTime scheduledAt
) {
}