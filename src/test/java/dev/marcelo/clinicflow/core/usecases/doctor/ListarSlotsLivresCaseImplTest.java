package dev.marcelo.clinicflow.core.usecases.doctor;

import dev.marcelo.clinicflow.core.entities.Appointment;
import dev.marcelo.clinicflow.core.entities.Doctor;
import dev.marcelo.clinicflow.core.entities.DoctorSchedule;
import dev.marcelo.clinicflow.core.enums.AppointmentStatus;
import dev.marcelo.clinicflow.core.enums.DoctorSpecialty;
import dev.marcelo.clinicflow.core.enums.Gender;
import dev.marcelo.clinicflow.core.exceptions.DoctorNotFoundException;
import dev.marcelo.clinicflow.core.gateway.AppointmentGateway;
import dev.marcelo.clinicflow.core.gateway.DoctorGateway;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarSlotsLivresCaseImplTest {

    private static final Long DOCTOR_ID = 1L;
    // 2026-06-15 cai numa segunda-feira (MONDAY)
    private static final LocalDate DATA = LocalDate.of(2026, 6, 15);

    @Mock
    private DoctorGateway doctorGateway;

    @Mock
    private DoctorScheduleGateway scheduleGateway;

    @Mock
    private AppointmentGateway appointmentGateway;

    @InjectMocks
    private ListarSlotsLivresCaseImpl listarSlotsLivresCase;

    private Doctor medico() {
        return new Doctor(
                DOCTOR_ID,
                "Ana",
                "Souza",
                "123.456.789-00",
                "ana.souza@clinicflow.dev",
                "Av. Paulista, 1000",
                "11988887777",
                42,
                "CRM-SP-123456",
                Gender.FEMALE,
                DoctorSpecialty.CARDIOLOGY,
                null
        );
    }

    private DoctorSchedule janela(LocalTime inicio, LocalTime fim, Integer slotMinutes) {
        return new DoctorSchedule(1L, DOCTOR_ID, DayOfWeek.MONDAY, inicio, fim, slotMinutes);
    }

    private Appointment consultaAs(LocalTime horario, AppointmentStatus status) {
        return new Appointment(1L, null, null, null, LocalDateTime.of(DATA, horario), status);
    }

    @Test
    void deveGerarSlotsSubtraindoConsultaExistente() {
        when(doctorGateway.buscarDoutor(DOCTOR_ID)).thenReturn(Optional.of(medico()));
        when(scheduleGateway.listarPorMedicoEDia(DOCTOR_ID, DayOfWeek.MONDAY))
                .thenReturn(List.of(janela(LocalTime.of(8, 0), LocalTime.of(12, 0), 30)));
        when(appointmentGateway.listarPorMedicoEData(DOCTOR_ID, DATA))
                .thenReturn(List.of(consultaAs(LocalTime.of(8, 0), AppointmentStatus.AGENDADA)));

        List<LocalTime> livres = listarSlotsLivresCase.execute(DOCTOR_ID, DATA);

        assertThat(livres).doesNotContain(LocalTime.of(8, 0));
        assertThat(livres).contains(LocalTime.of(8, 30), LocalTime.of(11, 30));
        // janela 08:00-12:00 em slots de 30min = 8 slots; menos a consulta das 08:00 = 7
        assertThat(livres).hasSize(7);
        assertThat(livres).doesNotContain(LocalTime.of(12, 0));
    }

    @Test
    void deveRetornarVazioQuandoSemJanelaNoDia() {
        when(doctorGateway.buscarDoutor(DOCTOR_ID)).thenReturn(Optional.of(medico()));
        when(scheduleGateway.listarPorMedicoEDia(DOCTOR_ID, DayOfWeek.MONDAY))
                .thenReturn(List.of());

        List<LocalTime> livres = listarSlotsLivresCase.execute(DOCTOR_ID, DATA);

        assertThat(livres).isEmpty();
        verify(appointmentGateway, never()).listarPorMedicoEData(any(), any());
    }

    @Test
    void deveRemoverTodosOsSlotsQueIntersectamConsultaForaDoGrid() {
        when(doctorGateway.buscarDoutor(DOCTOR_ID)).thenReturn(Optional.of(medico()));
        when(scheduleGateway.listarPorMedicoEDia(DOCTOR_ID, DayOfWeek.MONDAY))
                .thenReturn(List.of(janela(LocalTime.of(8, 0), LocalTime.of(10, 0), 30)));
        // Consulta às 08:15 (fora do grid), 30min → cobre [08:15, 08:45), intersecta 08:00 e 08:30.
        when(appointmentGateway.listarPorMedicoEData(DOCTOR_ID, DATA))
                .thenReturn(List.of(consultaAs(LocalTime.of(8, 15), AppointmentStatus.AGENDADA)));

        List<LocalTime> livres = listarSlotsLivresCase.execute(DOCTOR_ID, DATA);

        assertThat(livres).doesNotContain(LocalTime.of(8, 0), LocalTime.of(8, 30));
        assertThat(livres).containsExactly(LocalTime.of(9, 0), LocalTime.of(9, 30));
    }

    @Test
    void naoDeveRemoverSlotQuandoConsultaTerminaExatamenteNoInicioDele() {
        when(doctorGateway.buscarDoutor(DOCTOR_ID)).thenReturn(Optional.of(medico()));
        when(scheduleGateway.listarPorMedicoEDia(DOCTOR_ID, DayOfWeek.MONDAY))
                .thenReturn(List.of(janela(LocalTime.of(8, 30), LocalTime.of(10, 0), 30)));
        // Consulta às 08:00, 30min → cobre [08:00, 08:30); o slot 08:30 começa quando ela termina.
        when(appointmentGateway.listarPorMedicoEData(DOCTOR_ID, DATA))
                .thenReturn(List.of(consultaAs(LocalTime.of(8, 0), AppointmentStatus.AGENDADA)));

        List<LocalTime> livres = listarSlotsLivresCase.execute(DOCTOR_ID, DATA);

        assertThat(livres).contains(LocalTime.of(8, 30));
    }

    @Test
    void deveIgnorarConsultasComStatusInativoAoOcuparSlot() {
        when(doctorGateway.buscarDoutor(DOCTOR_ID)).thenReturn(Optional.of(medico()));
        when(scheduleGateway.listarPorMedicoEDia(DOCTOR_ID, DayOfWeek.MONDAY))
                .thenReturn(List.of(janela(LocalTime.of(8, 0), LocalTime.of(9, 0), 30)));
        when(appointmentGateway.listarPorMedicoEData(DOCTOR_ID, DATA))
                .thenReturn(List.of(consultaAs(LocalTime.of(8, 0), AppointmentStatus.CANCELADA)));

        List<LocalTime> livres = listarSlotsLivresCase.execute(DOCTOR_ID, DATA);

        assertThat(livres).containsExactly(LocalTime.of(8, 0), LocalTime.of(8, 30));
    }

    @Test
    void deveLancarNotFoundQuandoMedicoInexistente() {
        when(doctorGateway.buscarDoutor(DOCTOR_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> listarSlotsLivresCase.execute(DOCTOR_ID, DATA))
                .isInstanceOf(DoctorNotFoundException.class);

        verify(scheduleGateway, never()).listarPorMedicoEDia(any(), any());
    }
}
