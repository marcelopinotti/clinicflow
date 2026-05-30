package dev.marcelo.clinicflow.infrastructure.dtos;

import dev.marcelo.clinicflow.core.enums.DoctorSpecialty;


import java.util.Set;

public record ClinicRequest(
        Long id,
        String name,
        String address,
        String phone,
        String email,
        Set<DoctorSpecialty> specialties
) {
}
