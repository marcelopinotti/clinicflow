package dev.marcelo.clinicflow.core.usecases.appointment;

import dev.marcelo.clinicflow.core.entities.Appointment;
import dev.marcelo.clinicflow.core.gateway.AppointmentGateway;

import java.util.List;

public class ListarConsultasPorMedicoCaseImpl implements ListarConsultasPorMedicoCase {

    private final AppointmentGateway appointmentGateway;

    public ListarConsultasPorMedicoCaseImpl(AppointmentGateway appointmentGateway) {
        this.appointmentGateway = appointmentGateway;
    }

    @Override
    public List<Appointment> execute(Long doctorId) {
        return appointmentGateway.listarPorMedico(doctorId);
    }
}
