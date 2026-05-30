package dev.marcelo.clinicflow.core.usecases.clinic;

import dev.marcelo.clinicflow.core.gateway.ClinicGateway;

public class DeletarClinicaCaseImpl implements DeletarClinicaCase {

    private final ClinicGateway clinicGateway;

    public DeletarClinicaCaseImpl(ClinicGateway clinicGateway) {
        this.clinicGateway = clinicGateway;
    }

    @Override
    public void execute(Long id) {
    }
}
