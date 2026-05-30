package dev.marcelo.clinicflow.infrastructure.mapper;

import dev.marcelo.clinicflow.core.entities.Clinic;
import dev.marcelo.clinicflow.core.enums.ClinicStatus;
import dev.marcelo.clinicflow.infrastructure.dtos.ClinicRequest;
import dev.marcelo.clinicflow.infrastructure.dtos.ClinicResponse;

public class ClinicEntityMapper {
    public Clinic toEntity(ClinicRequest request) {
        return new Clinic(
                request.id(),
                request.name(),
                request.address(),
                request.phone(),
                request.email(),
                ClinicStatus.ACTIVE,
                request.specialties()
        );
    }

    public ClinicResponse toResponse(Clinic clinic) {
        return new ClinicResponse(
                clinic.id(),
                clinic.name(),
                clinic.address(),
                clinic.phone(),
                clinic.email(),
                clinic.status(),
                clinic.specialties()
        );
    }
}
