package dev.marcelo.clinicflow.infrastructure.gateway;

import dev.marcelo.clinicflow.core.gateway.PatientGateway;
import dev.marcelo.clinicflow.infrastructure.persistence.PatientRepository;
import org.springframework.stereotype.Component;

@Component
public class PatientRepositoryGateway implements PatientGateway {

    private final PatientRepository repository;

    public PatientRepositoryGateway(PatientRepository repository) {
        this.repository = repository;
    }



}
