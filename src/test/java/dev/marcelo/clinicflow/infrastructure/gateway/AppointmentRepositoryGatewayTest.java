package dev.marcelo.clinicflow.infrastructure.gateway;

import dev.marcelo.clinicflow.core.entities.Appointment;
import dev.marcelo.clinicflow.core.entities.Doctor;
import dev.marcelo.clinicflow.core.enums.AppointmentStatus;
import dev.marcelo.clinicflow.core.exceptions.DoctorTimeSlotTakenException;
import dev.marcelo.clinicflow.infrastructure.mapper.AppointmentEntityMapper;
import dev.marcelo.clinicflow.infrastructure.persistence.AppointmentEntity;
import dev.marcelo.clinicflow.infrastructure.persistence.AppointmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentRepositoryGatewayTest {

    @Mock
    private AppointmentRepository repository;

    @Mock
    private AppointmentEntityMapper mapper;

    @InjectMocks
    private AppointmentRepositoryGateway gateway;

    private Appointment consulta(Long id) {
        return new Appointment(id, null, null, null,
                LocalDateTime.of(2026, 6, 15, 9, 0), AppointmentStatus.AGENDADA);
    }

    @Test
    void deveMapearEntidadeESalvarAoSalvarConsulta() {
        Appointment entrada = consulta(null);
        Appointment salvaDominio = consulta(1L);
        AppointmentEntity entity = mock(AppointmentEntity.class);
        AppointmentEntity persisted = mock(AppointmentEntity.class);

        when(mapper.toEntity(entrada)).thenReturn(entity);
        when(repository.saveAndFlush(entity)).thenReturn(persisted);
        when(mapper.toDomain(persisted)).thenReturn(salvaDominio);

        Appointment resultado = gateway.salvar(entrada);

        assertThat(resultado).isSameAs(salvaDominio);
        verify(repository).saveAndFlush(entity);
    }

    @Test
    void salvarTraduzVioacaoDoIndiceDeSlotEm409() {
        // issue #22: a violação da unicidade de slot (corrida) vira a exceção de
        // domínio de slot ocupado, que o handler mapeia para 409.
        Appointment entrada = consultaComMedico(7L);
        AppointmentEntity entity = mock(AppointmentEntity.class);

        when(mapper.toEntity(entrada)).thenReturn(entity);
        when(repository.saveAndFlush(entity)).thenThrow(new DataIntegrityViolationException(
                "ERROR: duplicate key value violates unique constraint \"uq_consultas_medico_slot_ativa\""));

        assertThatExceptionOfType(DoctorTimeSlotTakenException.class)
                .isThrownBy(() -> gateway.salvar(entrada));
    }

    @Test
    void salvarRepropagaOutrasViolacoesDeIntegridade() {
        // Violações que não são do slot (ex.: FK) não devem virar "slot ocupado".
        Appointment entrada = consultaComMedico(7L);
        AppointmentEntity entity = mock(AppointmentEntity.class);

        when(mapper.toEntity(entrada)).thenReturn(entity);
        DataIntegrityViolationException outra = new DataIntegrityViolationException(
                "ERROR: insert violates foreign key constraint \"consultas_medico_id_fkey\"");
        when(repository.saveAndFlush(entity)).thenThrow(outra);

        assertThatExceptionOfType(DataIntegrityViolationException.class)
                .isThrownBy(() -> gateway.salvar(entrada))
                .isSameAs(outra);
    }

    private Appointment consultaComMedico(Long doctorId) {
        Doctor doctor = new Doctor(doctorId, "Ana", "Silva", "000.000.000-00",
                "ana@example.com", "Rua 1", "+5511999999999", 40, "CRM123",
                null, null, Set.of());
        return new Appointment(null, null, doctor, null,
                LocalDateTime.of(2026, 6, 15, 9, 0), AppointmentStatus.AGENDADA);
    }

    @Test
    void buscarPorIdRetornaVazioQuandoNaoEncontrada() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThat(gateway.buscarPorId(99L)).isEmpty();
    }

    @Test
    void listarPorMedicoEDataDeveConsultarJanelaDoDiaInteiro() {
        LocalDate data = LocalDate.of(2026, 6, 15);
        LocalDateTime inicio = data.atStartOfDay();
        LocalDateTime fim = data.atTime(LocalTime.MAX);
        AppointmentEntity entity = mock(AppointmentEntity.class);
        Appointment dominio = consulta(1L);

        when(repository.findByDoctorIdAndScheduledAtBetween(1L, inicio, fim))
                .thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(dominio);

        assertThat(gateway.listarPorMedicoEData(1L, data)).containsExactly(dominio);
    }

    @Test
    void existeConflitoNoIntervaloDelegaParaRepositorioIgnorandoCanceladas() {
        LocalDateTime inicio = LocalDateTime.of(2026, 6, 15, 8, 30);
        LocalDateTime fim = LocalDateTime.of(2026, 6, 15, 9, 30);
        when(repository.existeConflitoNoIntervalo(1L, AppointmentStatus.CANCELADA, inicio, fim, 7L))
                .thenReturn(true);

        assertThat(gateway.existeConflitoNoIntervalo(1L, inicio, fim, 7L)).isTrue();
    }

    @Test
    void listarPorPacienteMapeiaResultados() {
        AppointmentEntity entity = mock(AppointmentEntity.class);
        Appointment dominio = consulta(1L);
        when(repository.findByPatientId(7L)).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(dominio);

        assertThat(gateway.listarPorPaciente(7L)).containsExactly(dominio);
    }
}
