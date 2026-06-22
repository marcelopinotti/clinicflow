package dev.marcelo.clinicflow.core.services;

import dev.marcelo.clinicflow.core.entities.DoctorSchedule;
import dev.marcelo.clinicflow.core.exceptions.DoctorTimeSlotTakenException;
import dev.marcelo.clinicflow.core.exceptions.OutsideDoctorScheduleException;
import dev.marcelo.clinicflow.core.gateway.AppointmentGateway;
import dev.marcelo.clinicflow.core.gateway.DoctorScheduleGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgendaValidatorTest {

    private static final Long DOCTOR_ID = 20L;

    @Mock
    private AppointmentGateway appointmentGateway;
    @Mock
    private DoctorScheduleGateway scheduleGateway;

    @InjectMocks
    private AgendaValidator agendaValidator;

    private final LocalDate proximaSegunda = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));

    // Janela seg 09:00–12:00, slots de 30min → 09:00, 09:30, ..., 11:30.
    private DoctorSchedule janelaSegunda() {
        return new DoctorSchedule(1L, DOCTOR_ID, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(12, 0), 30);
    }

    @Test
    void naoLancaQuandoSlotValidoELivre() {
        LocalDateTime slot = proximaSegunda.atTime(9, 0);
        when(scheduleGateway.listarPorMedicoEDia(DOCTOR_ID, DayOfWeek.MONDAY)).thenReturn(List.of(janelaSegunda()));

        assertThatCode(() -> agendaValidator.validar(DOCTOR_ID, slot, null)).doesNotThrowAnyException();
    }

    @Test
    void lancaForaDaAgendaQuandoNaoHaJanelaNoDia() {
        LocalDateTime slot = proximaSegunda.atTime(9, 0);
        when(scheduleGateway.listarPorMedicoEDia(DOCTOR_ID, DayOfWeek.MONDAY)).thenReturn(List.of());

        assertThatThrownBy(() -> agendaValidator.validar(DOCTOR_ID, slot, null))
                .isInstanceOf(OutsideDoctorScheduleException.class);
    }

    @Test
    void lancaForaDaAgendaQuandoHorarioForaDoGrid() {
        LocalDateTime foraDoGrid = proximaSegunda.atTime(9, 15);
        when(scheduleGateway.listarPorMedicoEDia(DOCTOR_ID, DayOfWeek.MONDAY)).thenReturn(List.of(janelaSegunda()));

        assertThatThrownBy(() -> agendaValidator.validar(DOCTOR_ID, foraDoGrid, null))
                .isInstanceOf(OutsideDoctorScheduleException.class);
    }

    @Test
    void lancaSlotOcupadoQuandoHaConflitoNoIntervalo() {
        LocalDateTime slot = proximaSegunda.atTime(9, 0);
        when(scheduleGateway.listarPorMedicoEDia(DOCTOR_ID, DayOfWeek.MONDAY)).thenReturn(List.of(janelaSegunda()));
        when(appointmentGateway.existeConflitoNoIntervalo(
                DOCTOR_ID, slot.minusMinutes(30), slot.plusMinutes(30), null)).thenReturn(true);

        assertThatThrownBy(() -> agendaValidator.validar(DOCTOR_ID, slot, null))
                .isInstanceOf(DoctorTimeSlotTakenException.class);
    }

    @Test
    void derivaIntervaloDoSlotMinutesEPropagaIgnorarId() {
        LocalDateTime slot = proximaSegunda.atTime(10, 0);
        when(scheduleGateway.listarPorMedicoEDia(DOCTOR_ID, DayOfWeek.MONDAY)).thenReturn(List.of(janelaSegunda()));

        agendaValidator.validar(DOCTOR_ID, slot, 99L);

        // slot de 30min → intervalo aberto (09:30, 10:30); ignora a consulta 99 (reagendamento)
        verify(appointmentGateway).existeConflitoNoIntervalo(
                DOCTOR_ID, proximaSegunda.atTime(9, 30), proximaSegunda.atTime(10, 30), 99L);
    }
}
