package dev.marcelo.clinicflow.core.usecases.clinic;

import dev.marcelo.clinicflow.core.entities.Clinic;
import dev.marcelo.clinicflow.core.gateway.ClinicGateway;

import java.util.List;

public class ListarClinicasCaseImpl implements ListarClinicasCase {

    private final ClinicGateway clinicGateway;

    public ListarClinicasCaseImpl(ClinicGateway clinicGateway) {
        this.clinicGateway = clinicGateway;
    }

    @Override
    public List<Clinic> execute() {
        return List.of();
    }
}
