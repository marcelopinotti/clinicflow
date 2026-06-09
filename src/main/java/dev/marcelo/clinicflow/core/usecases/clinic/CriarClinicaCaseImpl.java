package dev.marcelo.clinicflow.core.usecases.clinic;

import dev.marcelo.clinicflow.core.entities.Clinic;
import dev.marcelo.clinicflow.core.exceptions.ClinicAlreadyExistsException;
import dev.marcelo.clinicflow.core.gateway.ClinicGateway;

public class CriarClinicaCaseImpl implements CriarClinicaCase {

    private final ClinicGateway clinicGateway;

    public CriarClinicaCaseImpl(ClinicGateway clinicGateway) {
        this.clinicGateway = clinicGateway;
    }

    @Override
    public Clinic execute(Clinic clinic) {
        if (clinicGateway.existePorCnpj(clinic.cnpj())) {
            throw new ClinicAlreadyExistsException(clinic.cnpj());
        }
        return clinicGateway.criarClinica(clinic);
    }
}
