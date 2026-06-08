package dev.marcelo.clinicflow.core.gateway;

import dev.marcelo.clinicflow.core.entities.Patient;

import java.util.List;
import java.util.Optional;

public interface PatientGateway {

    Patient criarPaciente(Patient patient);

    List<Patient> listarPacientes();

    Optional<Patient> buscarPaciente(Long id);

    Patient atualizarPaciente(Patient patient);

    void deletePaciente(Long id);
}
