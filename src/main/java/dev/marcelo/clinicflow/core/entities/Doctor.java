package dev.marcelo.clinicflow.core.entities;


import dev.marcelo.clinicflow.core.enums.DoctorSpecialty;
import dev.marcelo.clinicflow.core.enums.Gender;

public record Doctor(
        Long id,
        String name,
        String cpf,
        String email,
        String address,
        String phone,
        Integer age,
        String crm,
        Gender gender,
        DoctorSpecialty specialty
) {
}
