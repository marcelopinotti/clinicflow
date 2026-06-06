package dev.marcelo.clinicflow.infrastructure.mapper;

import dev.marcelo.clinicflow.core.entities.Appointment;
import dev.marcelo.clinicflow.core.entities.Clinic;
import dev.marcelo.clinicflow.core.entities.Doctor;
import dev.marcelo.clinicflow.core.entities.Patient;
import dev.marcelo.clinicflow.core.enums.AppointmentStatus;
import dev.marcelo.clinicflow.infrastructure.dtos.AppointmentRequest;
import dev.marcelo.clinicflow.infrastructure.dtos.AppointmentResponse;

public class AppointmentEntityMapper {
    public Appointment toEntity(AppointmentRequest request, Clinic clinic, Doctor doctor, Patient patient) {
        return new Appointment(
                request.id(),
                clinic,
                doctor,
                patient,
                request.scheduledAt(),
                AppointmentStatus.AGENDADA
        );
    }

    public AppointmentResponse toResponse(Appointment appointment) {
        var clinicId = appointment.clinic() == null ? null : appointment.clinic().id();
        var doctorId = appointment.doctor() == null ? null : appointment.doctor().id();
        var patientId = appointment.patient() == null ? null : appointment.patient().id();

        return new AppointmentResponse(
                appointment.id(),
                clinicId,
                doctorId,
                patientId,
                appointment.scheduledAt(),
                appointment.status()
        );

    }
}
