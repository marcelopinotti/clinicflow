package dev.marcelo.clinicflow.infrastructure.dtos;

import java.time.LocalDateTime;

public record AppointmentRequest(
        Long clinicId,
        Long doctorId,
        Long patientId,
        LocalDateTime scheduledAt
) {
}