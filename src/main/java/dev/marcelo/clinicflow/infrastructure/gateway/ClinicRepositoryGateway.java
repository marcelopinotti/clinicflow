package dev.marcelo.clinicflow.infrastructure.gateway;

import dev.marcelo.clinicflow.core.gateway.ClinicGateway;
import dev.marcelo.clinicflow.infrastructure.persistence.ClinicRepository;
import org.springframework.stereotype.Component;

@Component
public class ClinicRepositoryGateway implements ClinicGateway {

    private final ClinicRepository repository;

    public ClinicRepositoryGateway(ClinicRepository repository) {
        this.repository = repository;
    }


}
