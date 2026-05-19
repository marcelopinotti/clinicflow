package dev.marcelo.clinicflow.core.usecases.clinic;

import dev.marcelo.clinicflow.core.entities.Clinic;

public interface AtualizarClinicaCase {
    Clinic execute(Clinic clinic);
}
