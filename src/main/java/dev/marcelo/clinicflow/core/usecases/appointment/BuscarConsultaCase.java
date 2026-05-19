package dev.marcelo.clinicflow.core.usecases.appointment;

import dev.marcelo.clinicflow.core.entities.Appointment;

public interface BuscarConsultaCase {
    Appointment execute(Long id);
}
