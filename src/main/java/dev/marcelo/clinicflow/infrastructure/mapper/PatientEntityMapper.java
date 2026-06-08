package dev.marcelo.clinicflow.infrastructure.mapper;

import dev.marcelo.clinicflow.core.entities.Patient;
import dev.marcelo.clinicflow.infrastructure.persistence.PatientEntity;
import org.springframework.stereotype.Component;

@Component
public class PatientEntityMapper {


    public PatientEntity toEntity(Patient request) {
        return new PatientEntity(
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

    public Patient toDomain(PatientEntity patient) {
        return new Patient(
                patient.getId(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getCpf(),
                patient.getEmail(),
                patient.getAddress(),
                patient.getPhone(),
                patient.getAge(),
                patient.getGender()
        );
    }
}
