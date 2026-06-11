package dev.marcelo.clinicflow.core.entities;


import dev.marcelo.clinicflow.core.enums.DoctorSpecialty;
import dev.marcelo.clinicflow.core.enums.Gender;

import java.util.Set;

public record Doctor(
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
