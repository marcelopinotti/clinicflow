package dev.marcelo.clinicflow.infrastructure.dtos;

import dev.marcelo.clinicflow.core.enums.DoctorSpecialty;
import dev.marcelo.clinicflow.core.enums.Gender;

import java.util.Set;


public record DoctorRequest(
        Long id,
        String firstName,
        String lastName,
        String cpf,
        String email,
        String address,
        String phone,
        Integer age,
        String crm,
        Gender gender,
        DoctorSpecialty specialty,
        Set<Long> clinicIds
) {
}
