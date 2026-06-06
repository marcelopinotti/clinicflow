package dev.marcelo.clinicflow.core.usecases.appointment;

import dev.marcelo.clinicflow.core.entities.Appointment;
import dev.marcelo.clinicflow.core.gateway.AppointmentGateway;

public class CancelarConsultaCaseImpl implements CancelarConsultaCase {

    private final AppointmentGateway appointmentGateway;

    public CancelarConsultaCaseImpl(AppointmentGateway appointmentGateway) {
        this.appointmentGateway = appointmentGateway;
    }

    @Override
    public Appointment execute(Long id) {
        return null;
    }
}
