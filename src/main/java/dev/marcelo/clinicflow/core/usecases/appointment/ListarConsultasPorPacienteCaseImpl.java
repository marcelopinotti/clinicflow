package dev.marcelo.clinicflow.core.usecases.appointment;

import dev.marcelo.clinicflow.core.entities.Appointment;
import dev.marcelo.clinicflow.core.gateway.AppointmentGateway;

import java.util.List;

public class ListarConsultasPorPacienteCaseImpl implements ListarConsultasPorPacienteCase {

    private final AppointmentGateway appointmentGateway;

    public ListarConsultasPorPacienteCaseImpl(AppointmentGateway appointmentGateway) {
        this.appointmentGateway = appointmentGateway;
    }

    @Override
    public List<Appointment> execute(Long patientId) {
        return List.of();
    }
}
