package dev.marcelo.clinicflow.core.usecases.appointment;

import dev.marcelo.clinicflow.core.entities.Appointment;
import dev.marcelo.clinicflow.core.exceptions.AppointmentNotFoundException;
import dev.marcelo.clinicflow.core.exceptions.AppointmentNotReschedulableException;
import dev.marcelo.clinicflow.core.exceptions.InvalidAppointmentDateException;
import dev.marcelo.clinicflow.core.gateway.AppointmentGateway;
import dev.marcelo.clinicflow.core.services.AgendaValidator;

import java.time.LocalDateTime;

public class ReagendarConsultaCaseImpl implements ReagendarConsultaCase {

    private final AppointmentGateway appointmentGateway;
    private final AgendaValidator agendaValidator;

    public ReagendarConsultaCaseImpl(AppointmentGateway appointmentGateway, AgendaValidator agendaValidator) {
        this.appointmentGateway = appointmentGateway;
        this.agendaValidator = agendaValidator;
    }

    @Override
    public Appointment execute(Long id, LocalDateTime novaData) {
        Appointment appointment = appointmentGateway.buscarPorId(id)
                .orElseThrow(() -> new AppointmentNotFoundException(id));

        if (!appointment.status().isReschedulable()) {
            throw new AppointmentNotReschedulableException(appointment.status());
        }

        if (novaData == null || !novaData.isAfter(LocalDateTime.now())) {
            throw new InvalidAppointmentDateException(novaData);
        }

        if (!novaData.equals(appointment.scheduledAt())) {
            agendaValidator.validar(appointment.doctor().id(), novaData, appointment.id());
        }

        Appointment reagendada = new Appointment(
                appointment.id(),
                appointment.clinic(),
                appointment.doctor(),
                appointment.patient(),
                novaData,
                appointment.status()
        );
        return appointmentGateway.salvar(reagendada);
    }
}
