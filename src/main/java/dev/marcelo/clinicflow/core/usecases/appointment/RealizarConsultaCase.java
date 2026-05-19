package dev.marcelo.clinicflow.core.usecases.appointment;

import dev.marcelo.clinicflow.core.entities.Appointment;

public interface RealizarConsultaCase {
    Appointment execute(Long id);
}
