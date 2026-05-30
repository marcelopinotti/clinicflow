package dev.marcelo.clinicflow.infrastructure.mapper;

import dev.marcelo.clinicflow.core.entities.Doctor;
import dev.marcelo.clinicflow.infrastructure.persistence.DoctorEntity;
import org.springframework.stereotype.Component;

@Component
public class DoctorEntityMapper {
    public DoctorEntity toEntity(Doctor doctor) {
        return new DoctorEntity(
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

    public Doctor toDomain(DoctorEntity entity) {
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
                entity.getSpecialty()
        );
    }
}
