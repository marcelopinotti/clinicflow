package dev.marcelo.clinicflow.infrastructure.mapper;

import dev.marcelo.clinicflow.core.entities.Patient;
import dev.marcelo.clinicflow.infrastructure.dtos.PatientRequest;
import dev.marcelo.clinicflow.infrastructure.dtos.PatientResponse;

public class PatientEntityMapper {


    public Patient toEntity(PatientRequest request) {
        return new Patient(
                request.id(),
                request.firstName(),
                request.lastName(),
                request.cpf(),
                request.email(),
                request.address(),
                request.phone(),
                request.age(),
                request.gender()
        );
    }

    public PatientResponse toResponse(Patient patient) {
        return new PatientResponse(
                patient.id(),
                patient.firstName(),
                patient.lastName(),
                patient.cpf(),
                patient.email(),
                patient.address(),
                patient.phone(),
                patient.age(),
                patient.gender()
        );
    }
}
