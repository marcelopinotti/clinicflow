package dev.marcelo.clinicflow.core.usecases.appointment;

import dev.marcelo.clinicflow.core.entities.Appointment;
import dev.marcelo.clinicflow.core.enums.AppointmentStatus;
import dev.marcelo.clinicflow.core.exceptions.AppointmentNotYetOccurredException;
import dev.marcelo.clinicflow.core.exceptions.InvalidAppointmentStatusTransitionException;
import dev.marcelo.clinicflow.core.gateway.AppointmentGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class RealizarConsultaCaseImplTest {

    @Mock
    private AppointmentGateway appointmentGateway;

    @InjectMocks
    private RealizarConsultaCaseImpl realizarConsultaCase;

    private Appointment comStatusEHorario(AppointmentStatus status, LocalDateTime scheduledAt) {
        return new Appointment(1L, null, null, null, scheduledAt, status);
    }

    @Test
    void deveRealizarConsultaConfirmadaComHorarioJaPassado() {
        when(appointmentGateway.buscarPorId(1L))
                .thenReturn(Optional.of(comStatusEHorario(AppointmentStatus.CONFIRMADA, LocalDateTime.now().minusHours(1))));
        when(appointmentGateway.salvar(any(Appointment.class))).thenAnswer(i -> i.getArgument(0));

        Appointment resultado = realizarConsultaCase.execute(1L);

        assertThat(resultado.status()).isEqualTo(AppointmentStatus.REALIZADA);
    }

    @Test
    void deveLancarConflitoAoRealizarConsultaApenasAgendada() {
        when(appointmentGateway.buscarPorId(1L))
                .thenReturn(Optional.of(comStatusEHorario(AppointmentStatus.AGENDADA, LocalDateTime.now().minusHours(1))));

        assertThatThrownBy(() -> realizarConsultaCase.execute(1L))
                .isInstanceOf(InvalidAppointmentStatusTransitionException.class);

        verify(appointmentGateway, never()).salvar(any());
    }

    @Test
    void deveLancarConflitoAoRealizarConsultaComHorarioFuturo() {
        when(appointmentGateway.buscarPorId(1L))
                .thenReturn(Optional.of(comStatusEHorario(AppointmentStatus.CONFIRMADA, LocalDateTime.now().plusHours(1))));

        assertThatThrownBy(() -> realizarConsultaCase.execute(1L))
                .isInstanceOf(AppointmentNotYetOccurredException.class);

        verify(appointmentGateway, never()).salvar(any());
    }
}
