package dev.marcelo.clinicflow.core.usecases.patient;

import dev.marcelo.clinicflow.core.entities.Patient;

import java.util.List;

public interface ListarPacientesCase {
    List<Patient> execute();
}
