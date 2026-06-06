package dev.marcelo.clinicflow.infrastructure.mapper;

import dev.marcelo.clinicflow.core.entities.Doctor;
import dev.marcelo.clinicflow.infrastructure.dtos.DoctorRequest;
import dev.marcelo.clinicflow.infrastructure.dtos.DoctorResponse;
import org.springframework.stereotype.Component;

@Component
public class DoctorMapper {

    public Doctor toEntity(DoctorRequest request) {
        return new Doctor(
                request.id(),
                request.firstName(),
                request.lastName(),
                request.cpf(),
                request.email(),
                request.address(),
                request.phone(),
                request.age(),
                request.crm(),
                request.gender(),
                request.specialty()
        );
    }

    public DoctorResponse toResponse(Doctor doctor) {
        return new DoctorResponse(
                doctor.id(),
                doctor.firstName(),
                doctor.lastName(),
                doctor.cpf(),
                doctor.email(),
                doctor.address(),
                doctor.phone(),
                doctor.age(),
                doctor.crm(),
                doctor.gender(),
                doctor.specialty()
        );
    }
}