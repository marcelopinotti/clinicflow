package dev.marcelo.clinicflow.core.entities;


import dev.marcelo.clinicflow.core.enums.ClinicStatus;

public record Clinic(
        Long id,
        String name,
        String address,
        String phone,
        String email,
        ClinicStatus status
){}
