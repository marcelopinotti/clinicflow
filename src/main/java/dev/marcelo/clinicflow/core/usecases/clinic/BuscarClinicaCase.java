package dev.marcelo.clinicflow.core.usecases.clinic;

import dev.marcelo.clinicflow.core.entities.Clinic;

public interface BuscarClinicaCase {
    Clinic execute(Long id);
}
