package dev.marcelo.clinicflow.core.usecases.patient;

import dev.marcelo.clinicflow.core.entities.Patient;

public interface AtualizarPacienteCase {
    Patient execute(Patient patient);
}
