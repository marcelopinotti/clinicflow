package dev.marcelo.clinicflow.infrastructure.gateway;

import dev.marcelo.clinicflow.core.gateway.AppointmentGateway;
import dev.marcelo.clinicflow.infrastructure.persistence.AppointmentRepository;
import org.springframework.stereotype.Component;

@Component
public class AppointmentRepositoryGateway implements AppointmentGateway {

    private final AppointmentRepository repository;

    public AppointmentRepositoryGateway(AppointmentRepository repository) {
        this.repository = repository;
    }


}
