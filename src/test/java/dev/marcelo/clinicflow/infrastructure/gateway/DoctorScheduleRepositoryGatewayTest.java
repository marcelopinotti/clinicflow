package dev.marcelo.clinicflow.infrastructure.gateway;

import dev.marcelo.clinicflow.core.entities.DoctorSchedule;
import dev.marcelo.clinicflow.core.exceptions.DoctorNotFoundException;
import dev.marcelo.clinicflow.infrastructure.mapper.DoctorScheduleEntityMapper;
import dev.marcelo.clinicflow.infrastructure.persistence.DoctorEntity;
import dev.marcelo.clinicflow.infrastructure.persistence.DoctorRepository;
import dev.marcelo.clinicflow.infrastructure.persistence.DoctorScheduleEntity;
import dev.marcelo.clinicflow.infrastructure.persistence.DoctorScheduleRepository;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoctorScheduleRepositoryGatewayTest {

    @Mock
    private DoctorScheduleRepository repository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private DoctorScheduleEntityMapper mapper;

    @InjectMocks
    private DoctorScheduleRepositoryGateway gateway;

    private DoctorSchedule janela() {
        return new DoctorSchedule(null, 1L, DayOfWeek.MONDAY,
                LocalTime.of(8, 0), LocalTime.of(12, 0), 30);
    }

    @Test
    void deveSalvarAgendaQuandoMedicoExiste() {
        DoctorSchedule entrada = janela();
        DoctorSchedule salvaDominio = new DoctorSchedule(1L, 1L, DayOfWeek.MONDAY,
                LocalTime.of(8, 0), LocalTime.of(12, 0), 30);
        DoctorEntity doctor = mock(DoctorEntity.class);
        DoctorScheduleEntity entity = mock(DoctorScheduleEntity.class);
        DoctorScheduleEntity persisted = mock(DoctorScheduleEntity.class);

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(mapper.toEntity(entrada, doctor)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(persisted);
        when(mapper.toDomain(persisted)).thenReturn(salvaDominio);

        assertThat(gateway.salvar(entrada)).isSameAs(salvaDominio);
        verify(repository).save(entity);
    }

    @Test
    void deveLancarNotFoundENaoSalvarQuandoMedicoInexistente() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gateway.salvar(janela()))
                .isInstanceOf(DoctorNotFoundException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void listarPorMedicoEDiaMapeiaResultados() {
        DoctorScheduleEntity entity = mock(DoctorScheduleEntity.class);
        DoctorSchedule dominio = janela();
        when(repository.findByDoctor_IdAndDayOfWeek(1L, DayOfWeek.MONDAY))
                .thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(dominio);

        assertThat(gateway.listarPorMedicoEDia(1L, DayOfWeek.MONDAY)).containsExactly(dominio);
    }

    @Test
    void deletarDelegaAoRepositorio() {
        gateway.deletar(5L);

        verify(repository).deleteById(5L);
    }
}
