package dev.marcelo.clinicflow.core.usecases.appointment;

import dev.marcelo.clinicflow.core.entities.Appointment;

import java.util.List;

public class ListarConsultasPorPacienteCaseImpl implements ListarConsultasPorPacienteCase {

    @Override
    public List<Appointment> execute(Long patientId) {
        return List.of();
    }
}
