package dev.marcelo.clinicflow.infrastructure.dtos;

import dev.marcelo.clinicflow.core.enums.Gender;

public record PatientRequest(
        Long id,
        String firstName,
        String lastName,
        String cpf,
        String email,
        String address,
        String phone,
        Integer age,
        Gender gender

) {
}
