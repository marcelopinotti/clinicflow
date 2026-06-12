package dev.marcelo.clinicflow.core.usecases.appointment;

import dev.marcelo.clinicflow.core.entities.Appointment;
import dev.marcelo.clinicflow.core.entities.Clinic;
import dev.marcelo.clinicflow.core.entities.Doctor;
import dev.marcelo.clinicflow.core.entities.Patient;
import dev.marcelo.clinicflow.core.enums.AppointmentStatus;
import dev.marcelo.clinicflow.core.enums.ClinicStatus;
import dev.marcelo.clinicflow.core.enums.DoctorSpecialty;
import dev.marcelo.clinicflow.core.enums.Gender;
import dev.marcelo.clinicflow.core.exceptions.ClinicNotFoundException;
import dev.marcelo.clinicflow.core.exceptions.DoctorNotFoundException;
import dev.marcelo.clinicflow.core.exceptions.DoctorScheduleConflictException;
import dev.marcelo.clinicflow.core.exceptions.InvalidAppointmentDateException;
import dev.marcelo.clinicflow.core.exceptions.PatientNotFoundException;
import dev.marcelo.clinicflow.core.gateway.AppointmentGateway;
import dev.marcelo.clinicflow.core.gateway.ClinicGateway;
import dev.marcelo.clinicflow.core.gateway.DoctorGateway;
import dev.marcelo.clinicflow.core.gateway.PatientGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgendarConsultaCaseImplTest {

    @Mock
    private AppointmentGateway appointmentGateway;
    @Mock
    private ClinicGateway clinicGateway;
    @Mock
    private DoctorGateway doctorGateway;
    @Mock
    private PatientGateway patientGateway;

    @InjectMocks
    private AgendarConsultaCaseImpl agendarConsultaCase;

    private final LocalDateTime futuro = LocalDateTime.now().plusDays(1);

    private Clinic clinica() {
        return new Clinic(10L, "Clínica X", "12.345.678/0001-90", "Rua A", "1199", "x@x.com",
                ClinicStatus.values()[0], Set.of(DoctorSpecialty.values()[0]));
    }

    private Doctor medico() {
        return new Doctor(20L, "Ana", "Lima", "111", "ana@x.com", "Rua B", "1198", 40, "CRM1",
                Gender.values()[0], DoctorSpecialty.values()[0], null);
    }

    private Patient paciente() {
        return new Patient(30L, "Joao", "Souza", "222", "joao@x.com", "Rua C", "1197", 25, Gender.values()[0]);
    }

    private Appointment requisicao(LocalDateTime scheduledAt) {
        return new Appointment(
                null,
                new Clinic(10L, null, null, null, null, null, null, null),
                new Doctor(20L, null, null, null, null, null, null, null, null, null, null, null),
                new Patient(30L, null, null, null, null, null, null, null, null),
                scheduledAt,
                AppointmentStatus.AGENDADA
        );
    }

    @Test
    void deveAgendarConsultaComStatusAgendada() {
        when(clinicGateway.buscarClinica(10L)).thenReturn(Optional.of(clinica()));
        when(doctorGateway.buscarDoutor(20L)).thenReturn(Optional.of(medico()));
        when(patientGateway.buscarPaciente(30L)).thenReturn(Optional.of(paciente()));
        when(appointmentGateway.existeConflitoDeHorario(20L, futuro)).thenReturn(false);
        when(appointmentGateway.salvar(any(Appointment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Appointment resultado = agendarConsultaCase.execute(requisicao(futuro));

        ArgumentCaptor<Appointment> captor = ArgumentCaptor.forClass(Appointment.class);
        verify(appointmentGateway).salvar(captor.capture());
        assertThat(captor.getValue().status()).isEqualTo(AppointmentStatus.AGENDADA);
        assertThat(captor.getValue().clinic().name()).isEqualTo("Clínica X");
        assertThat(resultado.status()).isEqualTo(AppointmentStatus.AGENDADA);
    }

    @Test
    void deveLancar404QuandoClinicaInexistente() {
        when(clinicGateway.buscarClinica(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> agendarConsultaCase.execute(requisicao(futuro)))
                .isInstanceOf(ClinicNotFoundException.class);

        verify(appointmentGateway, never()).salvar(any());
    }

    @Test
    void deveLancar404QuandoMedicoInexistente() {
        when(clinicGateway.buscarClinica(10L)).thenReturn(Optional.of(clinica()));
        when(doctorGateway.buscarDoutor(20L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> agendarConsultaCase.execute(requisicao(futuro)))
                .isInstanceOf(DoctorNotFoundException.class);

        verify(appointmentGateway, never()).salvar(any());
    }

    @Test
    void deveLancar404QuandoPacienteInexistente() {
        when(clinicGateway.buscarClinica(10L)).thenReturn(Optional.of(clinica()));
        when(doctorGateway.buscarDoutor(20L)).thenReturn(Optional.of(medico()));
        when(patientGateway.buscarPaciente(30L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> agendarConsultaCase.execute(requisicao(futuro)))
                .isInstanceOf(PatientNotFoundException.class);

        verify(appointmentGateway, never()).salvar(any());
    }

    @Test
    void deveLancarErroQuandoDataNoPassado() {
        when(clinicGateway.buscarClinica(10L)).thenReturn(Optional.of(clinica()));
        when(doctorGateway.buscarDoutor(20L)).thenReturn(Optional.of(medico()));
        when(patientGateway.buscarPaciente(30L)).thenReturn(Optional.of(paciente()));

        assertThatThrownBy(() -> agendarConsultaCase.execute(requisicao(LocalDateTime.now().minusDays(1))))
                .isInstanceOf(InvalidAppointmentDateException.class);

        verify(appointmentGateway, never()).salvar(any());
    }

    @Test
    void deveLancarConflitoQuandoMedicoOcupadoNoMesmoHorario() {
        when(clinicGateway.buscarClinica(10L)).thenReturn(Optional.of(clinica()));
        when(doctorGateway.buscarDoutor(20L)).thenReturn(Optional.of(medico()));
        when(patientGateway.buscarPaciente(30L)).thenReturn(Optional.of(paciente()));
        when(appointmentGateway.existeConflitoDeHorario(20L, futuro)).thenReturn(true);

        assertThatThrownBy(() -> agendarConsultaCase.execute(requisicao(futuro)))
                .isInstanceOf(DoctorScheduleConflictException.class);

        verify(appointmentGateway, never()).salvar(any());
    }
}
