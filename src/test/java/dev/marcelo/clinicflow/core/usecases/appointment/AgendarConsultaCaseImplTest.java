package dev.marcelo.clinicflow.core.usecases.appointment;

import dev.marcelo.clinicflow.core.entities.Appointment;
import dev.marcelo.clinicflow.core.entities.Clinic;
import dev.marcelo.clinicflow.core.entities.Doctor;
import dev.marcelo.clinicflow.core.entities.DoctorSchedule;
import dev.marcelo.clinicflow.core.entities.Patient;
import dev.marcelo.clinicflow.core.enums.AppointmentStatus;
import dev.marcelo.clinicflow.core.enums.ClinicStatus;
import dev.marcelo.clinicflow.core.enums.DoctorSpecialty;
import dev.marcelo.clinicflow.core.enums.Gender;
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
import dev.marcelo.clinicflow.core.services.AgendaValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
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
    @Mock
    private DoctorScheduleGateway scheduleGateway;

    private AgendarConsultaCaseImpl agendarConsultaCase;

    @BeforeEach
    void setUp() {
        AgendaValidator agendaValidator = new AgendaValidator(appointmentGateway, scheduleGateway);
        agendarConsultaCase = new AgendarConsultaCaseImpl(appointmentGateway, clinicGateway, doctorGateway,
                patientGateway, agendaValidator);
    }

    // Próxima segunda-feira (sempre no futuro) usada como dia coberto pela janela do médico.
    private final LocalDate proximaSegunda = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
    private final LocalDateTime futuro = LocalDateTime.now().plusDays(1);

    private Clinic clinica() {
        return new Clinic(10L, "Clínica X", "12.345.678/0001-90", "Rua A", "1199", "x@x.com",
                ClinicStatus.values()[0], Set.of(DoctorSpecialty.values()[0]));
    }

    private Doctor medico() {
        return new Doctor(20L, "Ana", "Lima", "111", "ana@x.com", "Rua B", "1198", 40, "CRM1",
                Gender.values()[0], DoctorSpecialty.values()[0], Set.of());
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

    // Janela seg 09:00–12:00, slots de 30min → 09:00, 09:30, ..., 11:30.
    private DoctorSchedule janelaSegunda() {
        return new DoctorSchedule(1L, 20L, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(12, 0), 30);
    }

    private void stubEntidadesExistentes() {
        when(clinicGateway.buscarClinica(10L)).thenReturn(Optional.of(clinica()));
        when(doctorGateway.buscarDoutor(20L)).thenReturn(Optional.of(medico()));
        when(patientGateway.buscarPaciente(30L)).thenReturn(Optional.of(paciente()));
    }

    @Test
    void deveAgendarConsultaComStatusAgendadaQuandoSlotValidoELivre() {
        LocalDateTime slotValido = proximaSegunda.atTime(9, 0);
        stubEntidadesExistentes();
        when(scheduleGateway.listarPorMedicoEDia(20L, DayOfWeek.MONDAY)).thenReturn(List.of(janelaSegunda()));
        when(appointmentGateway.salvar(any(Appointment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Appointment resultado = agendarConsultaCase.execute(requisicao(slotValido));

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
        stubEntidadesExistentes();

        assertThatThrownBy(() -> agendarConsultaCase.execute(requisicao(LocalDateTime.now().minusDays(1))))
                .isInstanceOf(InvalidAppointmentDateException.class);

        verify(appointmentGateway, never()).salvar(any());
    }

    @Test
    void deveLancarForaDaAgendaQuandoMedicoNaoAtendeNoDia() {
        LocalDateTime slotValido = proximaSegunda.atTime(9, 0);
        stubEntidadesExistentes();
        when(scheduleGateway.listarPorMedicoEDia(20L, DayOfWeek.MONDAY)).thenReturn(List.of());

        assertThatThrownBy(() -> agendarConsultaCase.execute(requisicao(slotValido)))
                .isInstanceOf(OutsideDoctorScheduleException.class);

        verify(appointmentGateway, never()).salvar(any());
    }

    @Test
    void deveLancarForaDaAgendaQuandoHorarioForaDoGridDeSlots() {
        LocalDateTime foraDoGrid = proximaSegunda.atTime(9, 15);
        stubEntidadesExistentes();
        when(scheduleGateway.listarPorMedicoEDia(20L, DayOfWeek.MONDAY)).thenReturn(List.of(janelaSegunda()));

        assertThatThrownBy(() -> agendarConsultaCase.execute(requisicao(foraDoGrid)))
                .isInstanceOf(OutsideDoctorScheduleException.class);

        verify(appointmentGateway, never()).salvar(any());
    }

    @Test
    void deveLancarSlotOcupadoQuandoJaExisteConsultaAtivaNoHorario() {
        LocalDateTime slotValido = proximaSegunda.atTime(9, 0);
        stubEntidadesExistentes();
        when(scheduleGateway.listarPorMedicoEDia(20L, DayOfWeek.MONDAY)).thenReturn(List.of(janelaSegunda()));
        // slot de 30min em 09:00 → intervalo aberto verificado (08:30, 09:30); nova consulta não se ignora
        when(appointmentGateway.existeConflitoNoIntervalo(
                20L, proximaSegunda.atTime(8, 30), proximaSegunda.atTime(9, 30), null)).thenReturn(true);

        assertThatThrownBy(() -> agendarConsultaCase.execute(requisicao(slotValido)))
                .isInstanceOf(DoctorTimeSlotTakenException.class);

        verify(appointmentGateway, never()).salvar(any());
    }
}
