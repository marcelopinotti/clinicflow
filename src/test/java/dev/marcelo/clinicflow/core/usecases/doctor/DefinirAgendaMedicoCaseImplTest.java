package dev.marcelo.clinicflow.core.usecases.doctor;

import dev.marcelo.clinicflow.core.entities.Doctor;
import dev.marcelo.clinicflow.core.entities.DoctorSchedule;
import dev.marcelo.clinicflow.core.enums.DoctorSpecialty;
import dev.marcelo.clinicflow.core.enums.Gender;
import dev.marcelo.clinicflow.core.exceptions.DoctorNotFoundException;
import dev.marcelo.clinicflow.core.exceptions.InvalidScheduleWindowException;
import dev.marcelo.clinicflow.core.exceptions.OverlappingScheduleException;
import dev.marcelo.clinicflow.core.gateway.DoctorGateway;
import dev.marcelo.clinicflow.core.gateway.DoctorScheduleGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
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
class DefinirAgendaMedicoCaseImplTest {

    private static final Long DOCTOR_ID = 1L;

    @Mock
    private DoctorScheduleGateway scheduleGateway;

    @Mock
    private DoctorGateway doctorGateway;

    @InjectMocks
    private DefinirAgendaMedicoCaseImpl definirAgendaMedicoCase;

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
        return new DoctorSchedule(null, DOCTOR_ID, DayOfWeek.MONDAY, inicio, fim, slotMinutes);
    }

    @Test
    void deveSalvarJanelaQuandoDadosValidos() {
        DoctorSchedule entrada = janela(LocalTime.of(8, 0), LocalTime.of(12, 0), 30);
        DoctorSchedule salva = new DoctorSchedule(10L, DOCTOR_ID, DayOfWeek.MONDAY,
                LocalTime.of(8, 0), LocalTime.of(12, 0), 30);

        when(doctorGateway.buscarDoutor(DOCTOR_ID)).thenReturn(Optional.of(medico()));
        when(scheduleGateway.listarPorMedicoEDia(DOCTOR_ID, DayOfWeek.MONDAY)).thenReturn(List.of());
        when(scheduleGateway.salvar(entrada)).thenReturn(salva);

        DoctorSchedule resultado = definirAgendaMedicoCase.execute(entrada);

        assertThat(resultado.id()).isEqualTo(10L);
        verify(scheduleGateway).salvar(entrada);
    }

    @Test
    void deveLancarConflitoENaoSalvarQuandoJanelaSobrepoe() {
        DoctorSchedule existente = new DoctorSchedule(5L, DOCTOR_ID, DayOfWeek.MONDAY,
                LocalTime.of(8, 0), LocalTime.of(12, 0), 30);
        DoctorSchedule entrada = janela(LocalTime.of(8, 30), LocalTime.of(9, 30), 30);

        when(doctorGateway.buscarDoutor(DOCTOR_ID)).thenReturn(Optional.of(medico()));
        when(scheduleGateway.listarPorMedicoEDia(DOCTOR_ID, DayOfWeek.MONDAY)).thenReturn(List.of(existente));

        assertThatThrownBy(() -> definirAgendaMedicoCase.execute(entrada))
                .isInstanceOf(OverlappingScheduleException.class);

        verify(scheduleGateway, never()).salvar(any(DoctorSchedule.class));
    }

    @Test
    void deveLancarBadRequestQuandoStartMaiorOuIgualEnd() {
        DoctorSchedule entrada = janela(LocalTime.of(14, 0), LocalTime.of(13, 0), 30);

        when(doctorGateway.buscarDoutor(DOCTOR_ID)).thenReturn(Optional.of(medico()));

        assertThatThrownBy(() -> definirAgendaMedicoCase.execute(entrada))
                .isInstanceOf(InvalidScheduleWindowException.class);

        verify(scheduleGateway, never()).salvar(any(DoctorSchedule.class));
    }

    @Test
    void deveLancarBadRequestQuandoSlotNaoDivideJanela() {
        DoctorSchedule entrada = janela(LocalTime.of(8, 0), LocalTime.of(9, 0), 40);

        when(doctorGateway.buscarDoutor(DOCTOR_ID)).thenReturn(Optional.of(medico()));

        assertThatThrownBy(() -> definirAgendaMedicoCase.execute(entrada))
                .isInstanceOf(InvalidScheduleWindowException.class);

        verify(scheduleGateway, never()).salvar(any(DoctorSchedule.class));
    }

    @Test
    void deveLancarNotFoundQuandoMedicoInexistente() {
        DoctorSchedule entrada = janela(LocalTime.of(8, 0), LocalTime.of(12, 0), 30);

        when(doctorGateway.buscarDoutor(DOCTOR_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> definirAgendaMedicoCase.execute(entrada))
                .isInstanceOf(DoctorNotFoundException.class);

        verify(scheduleGateway, never()).salvar(any(DoctorSchedule.class));
    }
}
