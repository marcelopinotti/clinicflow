package dev.marcelo.clinicflow.core.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AppointmentStatusTest {

    @Test
    void agendadaPodeIrParaConfirmadaOuCancelada() {
        assertThat(AppointmentStatus.AGENDADA.canTransitionTo(AppointmentStatus.CONFIRMADA)).isTrue();
        assertThat(AppointmentStatus.AGENDADA.canTransitionTo(AppointmentStatus.CANCELADA)).isTrue();
    }

    @Test
    void agendadaNaoPodeIrDiretoParaRealizadaOuNoShow() {
        assertThat(AppointmentStatus.AGENDADA.canTransitionTo(AppointmentStatus.REALIZADA)).isFalse();
        assertThat(AppointmentStatus.AGENDADA.canTransitionTo(AppointmentStatus.NO_SHOW)).isFalse();
    }

    @Test
    void confirmadaPodeIrParaRealizadaNoShowOuCancelada() {
        assertThat(AppointmentStatus.CONFIRMADA.canTransitionTo(AppointmentStatus.REALIZADA)).isTrue();
        assertThat(AppointmentStatus.CONFIRMADA.canTransitionTo(AppointmentStatus.NO_SHOW)).isTrue();
        assertThat(AppointmentStatus.CONFIRMADA.canTransitionTo(AppointmentStatus.CANCELADA)).isTrue();
    }

    @Test
    void estadosFinaisNaoPermitemTransicoes() {
        for (AppointmentStatus target : AppointmentStatus.values()) {
            assertThat(AppointmentStatus.REALIZADA.canTransitionTo(target)).isFalse();
            assertThat(AppointmentStatus.CANCELADA.canTransitionTo(target)).isFalse();
            assertThat(AppointmentStatus.NO_SHOW.canTransitionTo(target)).isFalse();
        }
    }
}
