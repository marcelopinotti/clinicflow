package dev.marcelo.clinicflow.infrastructure.mapper;

import dev.marcelo.clinicflow.core.entities.Doctor;
import dev.marcelo.clinicflow.infrastructure.persistence.ClinicEntity;
import dev.marcelo.clinicflow.infrastructure.persistence.DoctorEntity;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DoctorEntityMapper {
    public DoctorEntity toEntity(Doctor doctor, Set<ClinicEntity> clinics) {
        DoctorEntity entity = new DoctorEntity(
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
        entity.setClinics(clinics);
        return entity;
    }

    public Doctor toDomain(DoctorEntity entity) {
        Set<Long> clinicIds = entity.getClinics() == null ? Set.of()
                : entity.getClinics().stream()
                        .map(ClinicEntity::getId)
                        .collect(Collectors.toSet());

        return new Doctor(
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getCpf(),
                entity.getEmail(),
                entity.getAddress(),
                entity.getPhone(),
                entity.getAge(),
                entity.getCrm(),
                entity.getGender(),
                entity.getSpecialty(),
                clinicIds
        );
    }
}