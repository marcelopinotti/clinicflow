package dev.marcelo.clinicflow.core.usecases.appointment;

import dev.marcelo.clinicflow.core.entities.Appointment;

import java.util.List;

public interface ListarConsultasPorPacienteCase {
    List<Appointment> execute(Long patientId);
}
