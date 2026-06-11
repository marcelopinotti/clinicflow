package dev.marcelo.clinicflow.infrastructure.gateway;

import dev.marcelo.clinicflow.core.entities.Doctor;
import dev.marcelo.clinicflow.core.gateway.DoctorGateway;
import dev.marcelo.clinicflow.infrastructure.mapper.DoctorEntityMapper;
import dev.marcelo.clinicflow.infrastructure.persistence.ClinicEntity;
import dev.marcelo.clinicflow.infrastructure.persistence.ClinicRepository;
import dev.marcelo.clinicflow.infrastructure.persistence.DoctorEntity;
import dev.marcelo.clinicflow.infrastructure.persistence.DoctorRepository;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class DoctorRepositoryGateway implements DoctorGateway {

    private final DoctorRepository repository;
    private final ClinicRepository clinicRepository;
    private final DoctorEntityMapper mapper;

    public DoctorRepositoryGateway(DoctorRepository repository, ClinicRepository clinicRepository, DoctorEntityMapper mapper) {
        this.repository = repository;
        this.clinicRepository = clinicRepository;
        this.mapper = mapper;
    }

    @Override
    public Doctor criarDoutor(Doctor doctor) {
        DoctorEntity entity = mapper.toEntity(doctor, resolveClinics(doctor.clinicIds()));
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
        DoctorEntity entity = mapper.toEntity(doctor, resolveClinics(doctor.clinicIds()));
        DoctorEntity updatedEntity = repository.save(entity);
        return mapper.toDomain(updatedEntity);
    }
    @Override
    public void deleteDoutor(Long id) {
        DoctorEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        repository.delete(entity);
    }

    private Set<ClinicEntity> resolveClinics(Set<Long> clinicIds) {
        if (clinicIds == null || clinicIds.isEmpty()) {
            return new HashSet<>();
        }
        List<ClinicEntity> found = clinicRepository.findAllById(clinicIds);
        if (found.size() != clinicIds.size()) {
            throw new RuntimeException("Uma ou mais clínicas informadas não foram encontradas");
        }
        return new HashSet<>(found);
    }


}