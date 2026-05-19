package dev.marcelo.clinicflow.core.usecases.clinic;

import dev.marcelo.clinicflow.core.entities.Clinic;

public interface CriarClinicaCase {
    Clinic execute(Clinic clinic);
}
