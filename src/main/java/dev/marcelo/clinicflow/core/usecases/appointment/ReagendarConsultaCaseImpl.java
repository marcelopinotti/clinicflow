package dev.marcelo.clinicflow.core.usecases.appointment;

import dev.marcelo.clinicflow.core.entities.Appointment;
import dev.marcelo.clinicflow.core.exceptions.AppointmentNotFoundException;
import dev.marcelo.clinicflow.core.exceptions.AppointmentNotReschedulableException;
import dev.marcelo.clinicflow.core.exceptions.DoctorScheduleConflictException;
import dev.marcelo.clinicflow.core.exceptions.InvalidAppointmentDateException;
import dev.marcelo.clinicflow.core.gateway.AppointmentGateway;

import java.time.LocalDateTime;

public class ReagendarConsultaCaseImpl implements ReagendarConsultaCase {

    private final AppointmentGateway appointmentGateway;

    public ReagendarConsultaCaseImpl(AppointmentGateway appointmentGateway) {
        this.appointmentGateway = appointmentGateway;
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

        if (!novaData.equals(appointment.scheduledAt())
                && appointmentGateway.existeConflitoDeHorario(appointment.doctor().id(), novaData)) {
            throw new DoctorScheduleConflictException(appointment.doctor().id(), novaData);
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
