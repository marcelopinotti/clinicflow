package dev.marcelo.clinicflow.core.gateway;

import dev.marcelo.clinicflow.core.entities.Clinic;

public interface ClinicGateway {

    Clinic criarClinica(Clinic clinic);

    boolean existePorCnpj(String cnpj);
}
