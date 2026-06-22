package dev.marcelo.clinicflow.core.usecases.appointment;

import dev.marcelo.clinicflow.core.entities.Appointment;
import dev.marcelo.clinicflow.core.enums.AppointmentStatus;
import dev.marcelo.clinicflow.core.exceptions.AppointmentNotFoundException;
import dev.marcelo.clinicflow.core.exceptions.AppointmentNotYetOccurredException;
import dev.marcelo.clinicflow.core.exceptions.InvalidAppointmentStatusTransitionException;
import dev.marcelo.clinicflow.core.gateway.AppointmentGateway;

import java.time.LocalDateTime;

public class RealizarConsultaCaseImpl implements RealizarConsultaCase {

    private final AppointmentGateway appointmentGateway;

    public RealizarConsultaCaseImpl(AppointmentGateway appointmentGateway) {
        this.appointmentGateway = appointmentGateway;
    }

    @Override
    public Appointment execute(Long id) {
        Appointment appointment = appointmentGateway.buscarPorId(id)
                .orElseThrow(() -> new AppointmentNotFoundException(id));

        if (!appointment.status().canTransitionTo(AppointmentStatus.REALIZADA)) {
            throw new InvalidAppointmentStatusTransitionException(appointment.status(), AppointmentStatus.REALIZADA);
        }

        if (appointment.scheduledAt().isAfter(LocalDateTime.now())) {
            throw new AppointmentNotYetOccurredException(AppointmentStatus.REALIZADA, appointment.scheduledAt());
        }

        Appointment realizada = new Appointment(
                appointment.id(),
                appointment.clinic(),
                appointment.doctor(),
                appointment.patient(),
                appointment.scheduledAt(),
                AppointmentStatus.REALIZADA
        );
        return appointmentGateway.salvar(realizada);
    }
}
