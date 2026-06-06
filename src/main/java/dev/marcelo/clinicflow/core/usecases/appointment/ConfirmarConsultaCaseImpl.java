package dev.marcelo.clinicflow.core.usecases.appointment;

import dev.marcelo.clinicflow.core.entities.Appointment;
import dev.marcelo.clinicflow.core.gateway.AppointmentGateway;

public class ConfirmarConsultaCaseImpl implements ConfirmarConsultaCase {

    private final AppointmentGateway appointmentGateway;

    public ConfirmarConsultaCaseImpl(AppointmentGateway appointmentGateway) {
        this.appointmentGateway = appointmentGateway;
    }

    @Override
    public Appointment execute(Long id) {
        return null;
    }
}
