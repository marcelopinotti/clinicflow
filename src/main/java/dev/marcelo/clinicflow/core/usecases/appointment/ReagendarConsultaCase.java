package dev.marcelo.clinicflow.core.usecases.appointment;

import dev.marcelo.clinicflow.core.entities.Appointment;

import java.time.LocalDateTime;

public interface ReagendarConsultaCase {

    Appointment execute(Long id, LocalDateTime novaData);
}
