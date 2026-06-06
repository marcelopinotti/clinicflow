package dev.marcelo.clinicflow.infrastructure.gateway;

import dev.marcelo.clinicflow.core.entities.Doctor;
import dev.marcelo.clinicflow.core.gateway.DoctorGateway;
import dev.marcelo.clinicflow.infrastructure.mapper.DoctorEntityMapper;
import dev.marcelo.clinicflow.infrastructure.persistence.DoctorEntity;
import dev.marcelo.clinicflow.infrastructure.persistence.DoctorRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

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

    @Override
    public List<Doctor> listarDoutor() {
        List<DoctorEntity> entities = repository.findAll();
        return entities.stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Doctor> buscarDoutor(Long id) {
        return repository.findById(id)
                .map(mapper::toDomain);



    }

    public Doctor atualizarDoutor(Doctor doctor) {
        DoctorEntity entity = mapper.toEntity(doctor);
        DoctorEntity updatedEntity = repository.save(entity);
        return mapper.toDomain(updatedEntity);
    }
    @Override
    public void deleteDoutor(Long id) {
        DoctorEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        repository.delete(entity);
    }


}
