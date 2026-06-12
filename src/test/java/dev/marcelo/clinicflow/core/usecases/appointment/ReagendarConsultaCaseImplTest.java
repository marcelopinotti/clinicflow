package dev.marcelo.clinicflow.core.usecases.appointment;

import dev.marcelo.clinicflow.core.entities.Appointment;
import dev.marcelo.clinicflow.core.entities.Doctor;
import dev.marcelo.clinicflow.core.enums.AppointmentStatus;
import dev.marcelo.clinicflow.core.exceptions.AppointmentNotFoundException;
import dev.marcelo.clinicflow.core.exceptions.AppointmentNotReschedulableException;
import dev.marcelo.clinicflow.core.exceptions.DoctorScheduleConflictException;
import dev.marcelo.clinicflow.core.exceptions.InvalidAppointmentDateException;
import dev.marcelo.clinicflow.core.gateway.AppointmentGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
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

    @InjectMocks
    private ReagendarConsultaCaseImpl reagendarConsultaCase;

    private final LocalDateTime horaAtual = LocalDateTime.now().plusDays(1);
    private final LocalDateTime novaData = LocalDateTime.now().plusDays(2);

    private Doctor medico() {
        return new Doctor(20L, null, null, null, null, null, null, null, null, null, null, null);
    }

    private Appointment consulta(AppointmentStatus status) {
        return new Appointment(1L, null, medico(), null, horaAtual, status);
    }

    @Test
    void deveReagendarPreservandoStatus() {
        when(appointmentGateway.buscarPorId(1L)).thenReturn(Optional.of(consulta(AppointmentStatus.CONFIRMADA)));
        when(appointmentGateway.existeConflitoDeHorario(20L, novaData)).thenReturn(false);
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
    void deveLancarConflitoQuandoMedicoOcupadoNoNovoHorario() {
        when(appointmentGateway.buscarPorId(1L)).thenReturn(Optional.of(consulta(AppointmentStatus.AGENDADA)));
        when(appointmentGateway.existeConflitoDeHorario(20L, novaData)).thenReturn(true);

        assertThatThrownBy(() -> reagendarConsultaCase.execute(1L, novaData))
                .isInstanceOf(DoctorScheduleConflictException.class);

        verify(appointmentGateway, never()).salvar(any());
    }

    @Test
    void naoDeveVerificarConflitoQuandoHorarioNaoMuda() {
        when(appointmentGateway.buscarPorId(1L)).thenReturn(Optional.of(consulta(AppointmentStatus.AGENDADA)));
        when(appointmentGateway.salvar(any(Appointment.class))).thenAnswer(i -> i.getArgument(0));

        Appointment resultado = reagendarConsultaCase.execute(1L, horaAtual);

        assertThat(resultado.scheduledAt()).isEqualTo(horaAtual);
        verify(appointmentGateway, never()).existeConflitoDeHorario(any(), any());
    }
}
