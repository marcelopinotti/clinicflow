package dev.marcelo.clinicflow.infrastructure.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.marcelo.clinicflow.core.enums.AppointmentStatus;
import dev.marcelo.clinicflow.infrastructure.utils.DatePatterns;

import java.time.LocalDateTime;

public record AppointmentResponse(
        Long id,
        Long clinicId,
        Long doctorId,
        Long patientId,
        @JsonFormat(pattern = DatePatterns.DATE_TIME)
        LocalDateTime scheduledAt,
        AppointmentStatus status
) {
}

