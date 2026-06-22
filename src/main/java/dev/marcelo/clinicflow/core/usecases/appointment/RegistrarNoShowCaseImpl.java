package dev.marcelo.clinicflow.core.usecases.appointment;

import dev.marcelo.clinicflow.core.entities.Appointment;
import dev.marcelo.clinicflow.core.enums.AppointmentStatus;
import dev.marcelo.clinicflow.core.exceptions.AppointmentNotFoundException;
import dev.marcelo.clinicflow.core.exceptions.AppointmentNotYetOccurredException;
import dev.marcelo.clinicflow.core.exceptions.InvalidAppointmentStatusTransitionException;
import dev.marcelo.clinicflow.core.gateway.AppointmentGateway;

import java.time.LocalDateTime;

public class RegistrarNoShowCaseImpl implements RegistrarNoShowCase {

    private final AppointmentGateway appointmentGateway;

    public RegistrarNoShowCaseImpl(AppointmentGateway appointmentGateway) {
        this.appointmentGateway = appointmentGateway;
    }

    @Override
    public Appointment execute(Long id) {
        Appointment appointment = appointmentGateway.buscarPorId(id)
                .orElseThrow(() -> new AppointmentNotFoundException(id));

        if (!appointment.status().canTransitionTo(AppointmentStatus.NO_SHOW)) {
            throw new InvalidAppointmentStatusTransitionException(appointment.status(), AppointmentStatus.NO_SHOW);
        }

        if (appointment.scheduledAt().isAfter(LocalDateTime.now())) {
            throw new AppointmentNotYetOccurredException(AppointmentStatus.NO_SHOW, appointment.scheduledAt());
        }

        Appointment noShow = new Appointment(
                appointment.id(),
                appointment.clinic(),
                appointment.doctor(),
                appointment.patient(),
                appointment.scheduledAt(),
                AppointmentStatus.NO_SHOW
        );
        return appointmentGateway.salvar(noShow);
    }
}
