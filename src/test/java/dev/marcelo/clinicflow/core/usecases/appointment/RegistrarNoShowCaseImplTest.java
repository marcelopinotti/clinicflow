package dev.marcelo.clinicflow.core.usecases.appointment;

import dev.marcelo.clinicflow.core.entities.Appointment;
import dev.marcelo.clinicflow.core.enums.AppointmentStatus;
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
class RegistrarNoShowCaseImplTest {

    @Mock
    private AppointmentGateway appointmentGateway;

    @InjectMocks
    private RegistrarNoShowCaseImpl registrarNoShowCase;

    private Appointment comStatus(AppointmentStatus status) {
        return new Appointment(1L, null, null, null, LocalDateTime.now().plusDays(1), status);
    }

    @Test
    void deveRegistrarNoShowParaConsultaConfirmada() {
        when(appointmentGateway.buscarPorId(1L)).thenReturn(Optional.of(comStatus(AppointmentStatus.CONFIRMADA)));
        when(appointmentGateway.salvar(any(Appointment.class))).thenAnswer(i -> i.getArgument(0));

        Appointment resultado = registrarNoShowCase.execute(1L);

        assertThat(resultado.status()).isEqualTo(AppointmentStatus.NO_SHOW);
    }

    @Test
    void deveLancarConflitoAoRegistrarNoShowParaConsultaApenasAgendada() {
        when(appointmentGateway.buscarPorId(1L)).thenReturn(Optional.of(comStatus(AppointmentStatus.AGENDADA)));

        assertThatThrownBy(() -> registrarNoShowCase.execute(1L))
                .isInstanceOf(InvalidAppointmentStatusTransitionException.class);

        verify(appointmentGateway, never()).salvar(any());
    }
}
