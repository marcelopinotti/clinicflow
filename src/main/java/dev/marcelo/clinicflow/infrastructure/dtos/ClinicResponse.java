package dev.marcelo.clinicflow.infrastructure.dtos;

import dev.marcelo.clinicflow.core.enums.ClinicStatus;
import dev.marcelo.clinicflow.core.enums.DoctorSpecialty;

import java.util.Set;

public record ClinicResponse(
        Long id,
        String name,
        String cnpj,
        String address,
        String phone,
        String email,
        ClinicStatus status,
        Set<DoctorSpecialty> specialties
) {
}
