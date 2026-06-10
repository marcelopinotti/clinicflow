package dev.marcelo.clinicflow.core.usecases.appointment;

import dev.marcelo.clinicflow.core.entities.Appointment;
import dev.marcelo.clinicflow.core.enums.AppointmentStatus;
import dev.marcelo.clinicflow.core.exceptions.AppointmentNotFoundException;
import dev.marcelo.clinicflow.core.exceptions.InvalidAppointmentStatusTransitionException;
import dev.marcelo.clinicflow.core.gateway.AppointmentGateway;

public class CancelarConsultaCaseImpl implements CancelarConsultaCase {

    private final AppointmentGateway appointmentGateway;

    public CancelarConsultaCaseImpl(AppointmentGateway appointmentGateway) {
        this.appointmentGateway = appointmentGateway;
    }

    @Override
    public Appointment execute(Long id) {
        Appointment appointment = appointmentGateway.buscarPorId(id)
                .orElseThrow(() -> new AppointmentNotFoundException(id));

        if (!appointment.status().canTransitionTo(AppointmentStatus.CANCELADA)) {
            throw new InvalidAppointmentStatusTransitionException(appointment.status(), AppointmentStatus.CANCELADA);
        }

        Appointment cancelada = new Appointment(
                appointment.id(),
                appointment.clinic(),
                appointment.doctor(),
                appointment.patient(),
                appointment.scheduledAt(),
                AppointmentStatus.CANCELADA
        );
        return appointmentGateway.salvar(cancelada);
    }
}
