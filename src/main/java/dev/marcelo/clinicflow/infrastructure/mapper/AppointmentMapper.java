package dev.marcelo.clinicflow.infrastructure.mapper;

import dev.marcelo.clinicflow.core.entities.Appointment;
import dev.marcelo.clinicflow.core.entities.Clinic;
import dev.marcelo.clinicflow.core.entities.Doctor;
import dev.marcelo.clinicflow.core.entities.Patient;
import dev.marcelo.clinicflow.core.enums.AppointmentStatus;
import dev.marcelo.clinicflow.infrastructure.dtos.AppointmentRequest;
import dev.marcelo.clinicflow.infrastructure.dtos.AppointmentResponse;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    public Appointment toEntity(AppointmentRequest request) {
        return new Appointment(
                request.id(),
                referenciaClinica(request.clinicId()),
                referenciaMedico(request.doctorId()),
                referenciaPaciente(request.patientId()),
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

    private Clinic referenciaClinica(Long id) {
        return id == null ? null : new Clinic(id, null, null, null, null, null, null, null);
    }

    private Doctor referenciaMedico(Long id) {
        return id == null ? null : new Doctor(id, null, null, null, null, null, null, null, null, null, null);
    }

    private Patient referenciaPaciente(Long id) {
        return id == null ? null : new Patient(id, null, null, null, null, null, null, null, null);
    }
}
