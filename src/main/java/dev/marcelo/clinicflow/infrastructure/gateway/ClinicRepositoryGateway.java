package dev.marcelo.clinicflow.infrastructure.gateway;

import dev.marcelo.clinicflow.core.entities.Clinic;
import dev.marcelo.clinicflow.core.gateway.ClinicGateway;
import dev.marcelo.clinicflow.infrastructure.mapper.ClinicEntityMapper;
import dev.marcelo.clinicflow.infrastructure.persistence.ClinicEntity;
import dev.marcelo.clinicflow.infrastructure.persistence.ClinicRepository;
import org.springframework.stereotype.Component;

@Component
public class ClinicRepositoryGateway implements ClinicGateway {

    private final ClinicRepository repository;
    private final ClinicEntityMapper mapper;

    public ClinicRepositoryGateway(ClinicRepository repository, ClinicEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Clinic criarClinica(Clinic clinic) {
        ClinicEntity entity = mapper.toEntity(clinic);
        ClinicEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public boolean existePorCnpj(String cnpj) {
        return repository.existsByCnpj(cnpj);
    }
}
