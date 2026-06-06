package dev.marcelo.clinicflow.core.usecases.appointment;

import dev.marcelo.clinicflow.core.entities.Appointment;
import dev.marcelo.clinicflow.core.gateway.AppointmentGateway;

public class AgendarConsultaCaseImpl implements AgendarConsultaCase {

    private final AppointmentGateway appointmentGateway;

    public AgendarConsultaCaseImpl(AppointmentGateway appointmentGateway) {
        this.appointmentGateway = appointmentGateway;
    }

    @Override
    public Appointment execute(Appointment appointment) {
        return null;
    }
}
