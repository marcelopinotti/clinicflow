package dev.marcelo.clinicflow.core.usecases.appointment;

import dev.marcelo.clinicflow.core.entities.Appointment;
import dev.marcelo.clinicflow.core.entities.Clinic;
import dev.marcelo.clinicflow.core.entities.Doctor;
import dev.marcelo.clinicflow.core.entities.DoctorSchedule;
import dev.marcelo.clinicflow.core.entities.Patient;
import dev.marcelo.clinicflow.core.enums.AppointmentStatus;
import dev.marcelo.clinicflow.core.exceptions.ClinicNotFoundException;
import dev.marcelo.clinicflow.core.exceptions.DoctorNotFoundException;
import dev.marcelo.clinicflow.core.exceptions.DoctorTimeSlotTakenException;
import dev.marcelo.clinicflow.core.exceptions.InvalidAppointmentDateException;
import dev.marcelo.clinicflow.core.exceptions.OutsideDoctorScheduleException;
import dev.marcelo.clinicflow.core.exceptions.PatientNotFoundException;
import dev.marcelo.clinicflow.core.gateway.AppointmentGateway;
import dev.marcelo.clinicflow.core.gateway.ClinicGateway;
import dev.marcelo.clinicflow.core.gateway.DoctorGateway;
import dev.marcelo.clinicflow.core.gateway.DoctorScheduleGateway;
import dev.marcelo.clinicflow.core.gateway.PatientGateway;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

public class AgendarConsultaCaseImpl implements AgendarConsultaCase {

    private static final Set<AppointmentStatus> STATUS_ATIVOS =
            Set.of(AppointmentStatus.AGENDADA, AppointmentStatus.CONFIRMADA);

    private final AppointmentGateway appointmentGateway;
    private final ClinicGateway clinicGateway;
    private final DoctorGateway doctorGateway;
    private final PatientGateway patientGateway;
    private final DoctorScheduleGateway scheduleGateway;

    public AgendarConsultaCaseImpl(AppointmentGateway appointmentGateway, ClinicGateway clinicGateway,
                                   DoctorGateway doctorGateway, PatientGateway patientGateway,
                                   DoctorScheduleGateway scheduleGateway) {
        this.appointmentGateway = appointmentGateway;
        this.clinicGateway = clinicGateway;
        this.doctorGateway = doctorGateway;
        this.patientGateway = patientGateway;
        this.scheduleGateway = scheduleGateway;
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

        validarAgenda(doctor.id(), scheduledAt);

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
    private void validarAgenda(Long doctorId, LocalDateTime scheduledAt) {
        DayOfWeek dayOfWeek = scheduledAt.getDayOfWeek();
        LocalTime horario = scheduledAt.toLocalTime();

        List<DoctorSchedule> janelas = scheduleGateway.listarPorMedicoEDia(doctorId, dayOfWeek);
        boolean inicioDeSlotValido = janelas.stream()
                .anyMatch(janela -> ehInicioDeSlot(janela, horario));
        if (!inicioDeSlotValido) {
            throw new OutsideDoctorScheduleException(doctorId, scheduledAt);
        }

        boolean slotOcupado = appointmentGateway.listarPorMedicoEData(doctorId, scheduledAt.toLocalDate()).stream()
                .filter(consulta -> STATUS_ATIVOS.contains(consulta.status()))
                .anyMatch(consulta -> consulta.scheduledAt().toLocalTime().equals(horario));
        if (slotOcupado) {
            throw new DoctorTimeSlotTakenException(doctorId, scheduledAt);
        }
    }

    private boolean ehInicioDeSlot(DoctorSchedule janela, LocalTime horario) {
        LocalTime slot = janela.startTime();
        while (!slot.plusMinutes(janela.slotMinutes()).isAfter(janela.endTime())) {
            if (slot.equals(horario)) {
                return true;
            }
            slot = slot.plusMinutes(janela.slotMinutes());
        }
        return false;
    }
}