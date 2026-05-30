package dev.marcelo.clinicflow.core.usecases.clinic;

import dev.marcelo.clinicflow.core.entities.Clinic;
import dev.marcelo.clinicflow.core.gateway.ClinicGateway;

public class BuscarClinicaCaseImpl implements BuscarClinicaCase {

    private final ClinicGateway clinicGateway;

    public BuscarClinicaCaseImpl(ClinicGateway clinicGateway) {
        this.clinicGateway = clinicGateway;
    }

    @Override
    public Clinic execute(Long id) {
        return null;
    }
}
