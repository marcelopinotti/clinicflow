package dev.marcelo.clinicflow.core.usecases.patient;

import dev.marcelo.clinicflow.core.entities.Patient;

import java.util.Optional;

public interface BuscarPacienteCase {
    Optional<Patient> execute(Long id);
}
