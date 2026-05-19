package dev.marcelo.clinicflow.core.usecases.appointment;

import dev.marcelo.clinicflow.core.entities.Appointment;

import java.util.List;

public interface ListarConsultasPorMedicoCase {
    List<Appointment> execute(Long doctorId);
}
