package dev.marcelo.clinicflow.core.usecases.appointment;

import dev.marcelo.clinicflow.core.entities.Appointment;
import dev.marcelo.clinicflow.core.enums.AppointmentStatus;
import dev.marcelo.clinicflow.core.exceptions.AppointmentNotFoundException;
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
class ConfirmarConsultaCaseImplTest {

    @Mock
    private AppointmentGateway appointmentGateway;

    @InjectMocks
    private ConfirmarConsultaCaseImpl confirmarConsultaCase;

    private Appointment comStatus(AppointmentStatus status) {
        return new Appointment(1L, null, null, null, LocalDateTime.now().plusDays(1), status);
    }

    @Test
    void deveConfirmarConsultaAgendada() {
        when(appointmentGateway.buscarPorId(1L)).thenReturn(Optional.of(comStatus(AppointmentStatus.AGENDADA)));
        when(appointmentGateway.salvar(any(Appointment.class))).thenAnswer(i -> i.getArgument(0));

        Appointment resultado = confirmarConsultaCase.execute(1L);

        assertThat(resultado.status()).isEqualTo(AppointmentStatus.CONFIRMADA);
    }

    @Test
    void deveLancarConflitoQuandoConsultaJaRealizada() {
        when(appointmentGateway.buscarPorId(1L)).thenReturn(Optional.of(comStatus(AppointmentStatus.REALIZADA)));

        assertThatThrownBy(() -> confirmarConsultaCase.execute(1L))
                .isInstanceOf(InvalidAppointmentStatusTransitionException.class);

        verify(appointmentGateway, never()).salvar(any());
    }

    @Test
    void deveLancarNotFoundQuandoConsultaInexistente() {
        when(appointmentGateway.buscarPorId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> confirmarConsultaCase.execute(1L))
                .isInstanceOf(AppointmentNotFoundException.class);
    }
}
