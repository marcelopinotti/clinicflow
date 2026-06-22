package dev.marcelo.clinicflow.core.usecases.appointment;

import dev.marcelo.clinicflow.core.entities.Appointment;
import dev.marcelo.clinicflow.core.entities.Doctor;
import dev.marcelo.clinicflow.core.entities.DoctorSchedule;
import dev.marcelo.clinicflow.core.enums.AppointmentStatus;
import dev.marcelo.clinicflow.core.exceptions.AppointmentNotFoundException;
import dev.marcelo.clinicflow.core.exceptions.AppointmentNotReschedulableException;
import dev.marcelo.clinicflow.core.exceptions.DoctorTimeSlotTakenException;
import dev.marcelo.clinicflow.core.exceptions.InvalidAppointmentDateException;
import dev.marcelo.clinicflow.core.exceptions.OutsideDoctorScheduleException;
import dev.marcelo.clinicflow.core.gateway.AppointmentGateway;
import dev.marcelo.clinicflow.core.gateway.DoctorScheduleGateway;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReagendarConsultaCaseImplTest {

    @Mock
    private AppointmentGateway appointmentGateway;
    @Mock
    private DoctorScheduleGateway scheduleGateway;

    private ReagendarConsultaCaseImpl reagendarConsultaCase;

    // Próxima segunda-feira (sempre no futuro) usada como dia coberto pela janela do médico.
    private final LocalDate proximaSegunda = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
    // Horário atual da consulta: slot válido na agenda (segunda 09:00).
    private final LocalDateTime horaAtual = proximaSegunda.atTime(9, 0);
    // Novo horário desejado: outro slot válido (segunda 10:00).
    private final LocalDateTime novaData = proximaSegunda.atTime(10, 0);

    @BeforeEach
    void setUp() {
        AgendaValidator agendaValidator = new AgendaValidator(appointmentGateway, scheduleGateway);
        reagendarConsultaCase = new ReagendarConsultaCaseImpl(appointmentGateway, agendaValidator);
    }

    private Doctor medico() {
        return new Doctor(20L, null, null, null, null, null, null, null, null, null, null, null);
    }

    private Appointment consulta(AppointmentStatus status) {
        return new Appointment(1L, null, medico(), null, horaAtual, status);
    }

    // Janela seg 09:00–12:00, slots de 30min → 09:00, 09:30, ..., 11:30.
    private DoctorSchedule janelaSegunda() {
        return new DoctorSchedule(1L, 20L, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(12, 0), 30);
    }

    @Test
    void deveReagendarPreservandoStatusQuandoSlotValidoELivre() {
        when(appointmentGateway.buscarPorId(1L)).thenReturn(Optional.of(consulta(AppointmentStatus.CONFIRMADA)));
        when(scheduleGateway.listarPorMedicoEDia(20L, DayOfWeek.MONDAY)).thenReturn(List.of(janelaSegunda()));
        when(appointmentGateway.salvar(any(Appointment.class))).thenAnswer(i -> i.getArgument(0));

        Appointment resultado = reagendarConsultaCase.execute(1L, novaData);

        ArgumentCaptor<Appointment> captor = ArgumentCaptor.forClass(Appointment.class);
        verify(appointmentGateway).salvar(captor.capture());
        assertThat(captor.getValue().scheduledAt()).isEqualTo(novaData);
        assertThat(captor.getValue().status()).isEqualTo(AppointmentStatus.CONFIRMADA);
        assertThat(resultado.scheduledAt()).isEqualTo(novaData);
        assertThat(resultado.status()).isEqualTo(AppointmentStatus.CONFIRMADA);
    }

    @Test
    void deveLancar404QuandoConsultaInexistente() {
        when(appointmentGateway.buscarPorId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reagendarConsultaCase.execute(1L, novaData))
                .isInstanceOf(AppointmentNotFoundException.class);

        verify(appointmentGateway, never()).salvar(any());
    }

    @Test
    void deveLancarConflitoQuandoStatusNaoPermiteReagendamento() {
        when(appointmentGateway.buscarPorId(1L)).thenReturn(Optional.of(consulta(AppointmentStatus.REALIZADA)));

        assertThatThrownBy(() -> reagendarConsultaCase.execute(1L, novaData))
                .isInstanceOf(AppointmentNotReschedulableException.class);

        verify(appointmentGateway, never()).salvar(any());
    }

    @Test
    void deveLancarErroQuandoNovaDataNoPassado() {
        when(appointmentGateway.buscarPorId(1L)).thenReturn(Optional.of(consulta(AppointmentStatus.AGENDADA)));

        assertThatThrownBy(() -> reagendarConsultaCase.execute(1L, LocalDateTime.now().minusDays(1)))
                .isInstanceOf(InvalidAppointmentDateException.class);

        verify(appointmentGateway, never()).salvar(any());
    }

    @Test
    void deveLancarForaDaAgendaQuandoMedicoNaoAtendeNaJanela() {
        when(appointmentGateway.buscarPorId(1L)).thenReturn(Optional.of(consulta(AppointmentStatus.AGENDADA)));
        when(scheduleGateway.listarPorMedicoEDia(20L, DayOfWeek.MONDAY)).thenReturn(List.of());

        assertThatThrownBy(() -> reagendarConsultaCase.execute(1L, novaData))
                .isInstanceOf(OutsideDoctorScheduleException.class);

        verify(appointmentGateway, never()).salvar(any());
    }

    @Test
    void deveLancarForaDaAgendaQuandoHorarioForaDoGridDeSlots() {
        LocalDateTime foraDoGrid = proximaSegunda.atTime(10, 15);
        when(appointmentGateway.buscarPorId(1L)).thenReturn(Optional.of(consulta(AppointmentStatus.AGENDADA)));
        when(scheduleGateway.listarPorMedicoEDia(20L, DayOfWeek.MONDAY)).thenReturn(List.of(janelaSegunda()));

        assertThatThrownBy(() -> reagendarConsultaCase.execute(1L, foraDoGrid))
                .isInstanceOf(OutsideDoctorScheduleException.class);

        verify(appointmentGateway, never()).salvar(any());
    }

    @Test
    void deveLancarSlotOcupadoQuandoOutraConsultaAtivaNoNovoHorario() {
        when(appointmentGateway.buscarPorId(1L)).thenReturn(Optional.of(consulta(AppointmentStatus.AGENDADA)));
        when(scheduleGateway.listarPorMedicoEDia(20L, DayOfWeek.MONDAY)).thenReturn(List.of(janelaSegunda()));
        // slot de 30min em 10:00 → intervalo aberto (09:30, 10:30); ignora a própria consulta (id 1)
        when(appointmentGateway.existeConflitoNoIntervalo(
                20L, proximaSegunda.atTime(9, 30), proximaSegunda.atTime(10, 30), 1L)).thenReturn(true);

        assertThatThrownBy(() -> reagendarConsultaCase.execute(1L, novaData))
                .isInstanceOf(DoctorTimeSlotTakenException.class);

        verify(appointmentGateway, never()).salvar(any());
    }

    @Test
    void naoDeveValidarAgendaQuandoHorarioNaoMuda() {
        when(appointmentGateway.buscarPorId(1L)).thenReturn(Optional.of(consulta(AppointmentStatus.AGENDADA)));
        when(appointmentGateway.salvar(any(Appointment.class))).thenAnswer(i -> i.getArgument(0));

        Appointment resultado = reagendarConsultaCase.execute(1L, horaAtual);

        assertThat(resultado.scheduledAt()).isEqualTo(horaAtual);
        verify(scheduleGateway, never()).listarPorMedicoEDia(any(), any());
    }
}
