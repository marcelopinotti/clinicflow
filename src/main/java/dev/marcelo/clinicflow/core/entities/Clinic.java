package dev.marcelo.clinicflow.core.entities;


import dev.marcelo.clinicflow.core.enums.ClinicStatus;
import dev.marcelo.clinicflow.core.enums.DoctorSpecialty;

import java.util.Set;

public record Clinic(
        Long id,
        String name,
        String cnpj,
        String address,
        String phone,
        String email,
        ClinicStatus status,
        Set<DoctorSpecialty> specialties
){}
