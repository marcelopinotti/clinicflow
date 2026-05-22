package dev.marcelo.clinicflow.infrastructure.mapper;

import dev.marcelo.clinicflow.core.entities.Doctor;
import dev.marcelo.clinicflow.infrastructure.dtos.DoctorAdminResponse;
import dev.marcelo.clinicflow.infrastructure.dtos.DoctorPublicResponse;
import dev.marcelo.clinicflow.infrastructure.dtos.DoctorRequest;
import org.springframework.stereotype.Component;

@Component
public class DoctorMapper {

    public Doctor toEntity(DoctorRequest request) {
        return new Doctor(
                null,
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

    public DoctorPublicResponse toPublicResponse(Doctor doctor) {
        return new DoctorPublicResponse(
                doctor.id(),
                doctor.firstName(),
                doctor.lastName(),
                doctor.crm(),
                doctor.specialty()
        );
    }

    public DoctorAdminResponse toAdminResponse(Doctor doctor) {
        return new DoctorAdminResponse(
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