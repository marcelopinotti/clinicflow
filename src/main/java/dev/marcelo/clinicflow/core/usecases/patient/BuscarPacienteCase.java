package dev.marcelo.clinicflow.core.usecases.patient;

import dev.marcelo.clinicflow.core.entities.Patient;

public interface BuscarPacienteCase {
    Patient execute(Long id);
}
