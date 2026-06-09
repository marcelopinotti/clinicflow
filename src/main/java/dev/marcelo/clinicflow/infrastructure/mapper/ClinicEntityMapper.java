package dev.marcelo.clinicflow.infrastructure.mapper;

import dev.marcelo.clinicflow.core.entities.Clinic;
import dev.marcelo.clinicflow.infrastructure.persistence.ClinicEntity;
import org.springframework.stereotype.Component;

@Component
public class ClinicEntityMapper {

    public ClinicEntity toEntity(Clinic clinic) {
        return new ClinicEntity(
                clinic.id(),
                clinic.name(),
                clinic.cnpj(),
                clinic.address(),
                clinic.phone(),
                clinic.email(),
                clinic.status(),
                clinic.specialties()
        );
    }

    public Clinic toDomain(ClinicEntity entity) {
        return new Clinic(
                entity.getId(),
                entity.getName(),
                entity.getCnpj(),
                entity.getAddress(),
                entity.getPhone(),
                entity.getEmail(),
                entity.getStatus(),
                entity.getSpecialties()
        );
    }
}
