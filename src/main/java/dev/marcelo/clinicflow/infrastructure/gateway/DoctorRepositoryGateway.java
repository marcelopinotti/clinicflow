package dev.marcelo.clinicflow.infrastructure.gateway;

import dev.marcelo.clinicflow.core.entities.Doctor;
import dev.marcelo.clinicflow.core.gateway.DoctorGateway;
import dev.marcelo.clinicflow.infrastructure.mapper.DoctorEntityMapper;
import dev.marcelo.clinicflow.infrastructure.persistence.DoctorEntity;
import dev.marcelo.clinicflow.infrastructure.persistence.DoctorRepository;
import org.springframework.stereotype.Component;

@Component
public class DoctorRepositoryGateway implements DoctorGateway {

    private final DoctorRepository repository;
    private final DoctorEntityMapper mapper;

    public DoctorRepositoryGateway(DoctorRepository repository, DoctorEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Doctor criarDoutor(Doctor doctor) {
        DoctorEntity entity = mapper.toEntity(doctor);
        DoctorEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }


}
