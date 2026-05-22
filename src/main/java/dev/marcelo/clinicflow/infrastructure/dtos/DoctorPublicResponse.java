package dev.marcelo.clinicflow.infrastructure.dtos;

import dev.marcelo.clinicflow.core.enums.DoctorSpecialty;

public record DoctorPublicResponse(
        Long id,
        String firstName,
        String lastName,
        String crm,
        DoctorSpecialty specialty
) {
}