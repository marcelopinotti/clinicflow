package dev.marcelo.clinicflow.core.usecases.appointment;

import dev.marcelo.clinicflow.core.entities.Appointment;
import dev.marcelo.clinicflow.core.enums.AppointmentStatus;
import dev.marcelo.clinicflow.core.exceptions.AppointmentNotFoundException;
import dev.marcelo.clinicflow.core.exceptions.InvalidAppointmentStatusTransitionException;
import dev.marcelo.clinicflow.core.gateway.AppointmentGateway;

public class ConfirmarConsultaCaseImpl implements ConfirmarConsultaCase {

    private final AppointmentGateway appointmentGateway;

    public ConfirmarConsultaCaseImpl(AppointmentGateway appointmentGateway) {
        this.appointmentGateway = appointmentGateway;
    }

    @Override
    public Appointment execute(Long id) {
        Appointment appointment = appointmentGateway.buscarPorId(id)
                .orElseThrow(() -> new AppointmentNotFoundException(id));

        if (!appointment.status().canTransitionTo(AppointmentStatus.CONFIRMADA)) {
            throw new InvalidAppointmentStatusTransitionException(appointment.status(), AppointmentStatus.CONFIRMADA);
        }

        Appointment confirmada = new Appointment(
                appointment.id(),
                appointment.clinic(),
                appointment.doctor(),
                appointment.patient(),
                appointment.scheduledAt(),
                AppointmentStatus.CONFIRMADA
        );
        return appointmentGateway.salvar(confirmada);
    }
}
