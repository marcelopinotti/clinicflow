package dev.marcelo.clinicflow.core.gateway;

import dev.marcelo.clinicflow.core.entities.Clinic;

import java.util.Optional;

public interface ClinicGateway {

    Clinic criarClinica(Clinic clinic);

    boolean existePorCnpj(String cnpj);

    Optional<Clinic> buscarClinica(Long id);
}
