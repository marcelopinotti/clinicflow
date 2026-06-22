package dev.marcelo.clinicflow.core.usecases.appointment;

import dev.marcelo.clinicflow.core.entities.Appointment;
import dev.marcelo.clinicflow.core.entities.Clinic;
import dev.marcelo.clinicflow.core.entities.Doctor;
import dev.marcelo.clinicflow.core.entities.Patient;
import dev.marcelo.clinicflow.core.enums.AppointmentStatus;
import dev.marcelo.clinicflow.core.exceptions.ClinicNotFoundException;
import dev.marcelo.clinicflow.core.exceptions.DoctorNotFoundException;
import dev.marcelo.clinicflow.core.exceptions.InvalidAppointmentDateException;
import dev.marcelo.clinicflow.core.exceptions.PatientNotFoundException;
import dev.marcelo.clinicflow.core.gateway.AppointmentGateway;
import dev.marcelo.clinicflow.core.gateway.ClinicGateway;
import dev.marcelo.clinicflow.core.gateway.DoctorGateway;
import dev.marcelo.clinicflow.core.gateway.PatientGateway;
import dev.marcelo.clinicflow.core.services.AgendaValidator;

import java.time.LocalDateTime;

public class AgendarConsultaCaseImpl implements AgendarConsultaCase {

    private final AppointmentGateway appointmentGateway;
    private final ClinicGateway clinicGateway;
    private final DoctorGateway doctorGateway;
    private final PatientGateway patientGateway;
    private final AgendaValidator agendaValidator;

    public AgendarConsultaCaseImpl(AppointmentGateway appointmentGateway, ClinicGateway clinicGateway,
                                   DoctorGateway doctorGateway, PatientGateway patientGateway,
                                   AgendaValidator agendaValidator) {
        this.appointmentGateway = appointmentGateway;
        this.clinicGateway = clinicGateway;
        this.doctorGateway = doctorGateway;
        this.patientGateway = patientGateway;
        this.agendaValidator = agendaValidator;
    }

    @Override
    public Appointment execute(Appointment appointment) {
        Clinic clinic = clinicGateway.buscarClinica(appointment.clinic().id())
                .orElseThrow(() -> new ClinicNotFoundException(appointment.clinic().id()));
        Doctor doctor = doctorGateway.buscarDoutor(appointment.doctor().id())
                .orElseThrow(() -> new DoctorNotFoundException(appointment.doctor().id()));
        Patient patient = patientGateway.buscarPaciente(appointment.patient().id())
                .orElseThrow(() -> new PatientNotFoundException(appointment.patient().id()));

        LocalDateTime scheduledAt = appointment.scheduledAt();
        if (scheduledAt == null || !scheduledAt.isAfter(LocalDateTime.now())) {
            throw new InvalidAppointmentDateException(scheduledAt);
        }

        agendaValidator.validar(doctor.id(), scheduledAt, null);

        Appointment paraAgendar = new Appointment(
                null,
                clinic,
                doctor,
                patient,
                scheduledAt,
                AppointmentStatus.AGENDADA
        );
        return appointmentGateway.salvar(paraAgendar);
    }
}
