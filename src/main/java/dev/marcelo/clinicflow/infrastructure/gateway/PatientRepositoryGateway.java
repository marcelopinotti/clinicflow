package dev.marcelo.clinicflow.infrastructure.gateway;

import dev.marcelo.clinicflow.core.entities.Patient;
import dev.marcelo.clinicflow.core.gateway.PatientGateway;
import dev.marcelo.clinicflow.infrastructure.mapper.PatientEntityMapper;
import dev.marcelo.clinicflow.infrastructure.persistence.PatientEntity;
import dev.marcelo.clinicflow.infrastructure.persistence.PatientRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PatientRepositoryGateway implements PatientGateway {

    private final PatientRepository repository;
    private final PatientEntityMapper mapper;

    public PatientRepositoryGateway(PatientRepository repository, PatientEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }


    @Override
    public Patient criarPaciente(Patient patient) {
        PatientEntity entity = mapper.toEntity(patient);
        PatientEntity save = repository.save(entity);
        return mapper.toDomain(save);
    }

    @Override
    public List<Patient> listarPacientes() {
        List<PatientEntity> entities = repository.findAll();
        return entities.stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Patient> buscarPaciente(Long id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Patient atualizarPaciente(Patient patient) {
        PatientEntity entity = mapper.toEntity(patient);
        PatientEntity updatedEntity = repository.save(entity);
        return mapper.toDomain(updatedEntity);
    }

    @Override
    public void deletePaciente(Long id) {
        PatientEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        repository.delete(entity);

    }
}
