package dev.marcelo.clinicflow.infrastructure.gateway;

import dev.marcelo.clinicflow.core.entities.Doctor;
import dev.marcelo.clinicflow.core.enums.DoctorSpecialty;
import dev.marcelo.clinicflow.core.enums.Gender;
import dev.marcelo.clinicflow.infrastructure.mapper.DoctorEntityMapper;
import dev.marcelo.clinicflow.infrastructure.persistence.ClinicEntity;
import dev.marcelo.clinicflow.infrastructure.persistence.ClinicRepository;
import dev.marcelo.clinicflow.infrastructure.persistence.DoctorEntity;
import dev.marcelo.clinicflow.infrastructure.persistence.DoctorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoctorRepositoryGatewayTest {

    @Mock
    private DoctorRepository repository;

    @Mock
    private ClinicRepository clinicRepository;

    @Mock
    private DoctorEntityMapper mapper;

    @InjectMocks
    private DoctorRepositoryGateway gateway;

    private Doctor medico(Set<Long> clinicIds) {
        return new Doctor(null, "Ana", "Souza", "123.456.789-00", "ana@b.com",
                "Rua 1", "11999", 40, "CRM-SP-1", Gender.FEMALE,
                DoctorSpecialty.CARDIOLOGY, clinicIds);
    }

    @Test
    void deveResolverClinicasAssociadasAoCriarMedico() {
        Doctor entrada = medico(Set.of(1L, 2L));
        Doctor salvoDominio = medico(Set.of(1L, 2L));
        DoctorEntity entity = mock(DoctorEntity.class);
        DoctorEntity persisted = mock(DoctorEntity.class);

        when(clinicRepository.findAllById(Set.of(1L, 2L)))
                .thenReturn(List.of(mock(ClinicEntity.class), mock(ClinicEntity.class)));
        when(mapper.toEntity(eq(entrada), anySet())).thenReturn(entity);
        when(repository.save(entity)).thenReturn(persisted);
        when(mapper.toDomain(persisted)).thenReturn(salvoDominio);

        assertThat(gateway.criarDoutor(entrada)).isSameAs(salvoDominio);
        verify(repository).save(entity);
    }

    @Test
    void deveLancarErroENaoSalvarQuandoAlgumaClinicaNaoExiste() {
        Doctor entrada = medico(Set.of(1L, 2L));
        when(clinicRepository.findAllById(Set.of(1L, 2L)))
                .thenReturn(List.of(mock(ClinicEntity.class)));

        assertThatThrownBy(() -> gateway.criarDoutor(entrada))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("não foram encontradas");

        verify(repository, never()).save(any());
    }

    @Test
    void naoDeveConsultarClinicasQuandoClinicIdsNulo() {
        Doctor entrada = medico(null);
        DoctorEntity entity = mock(DoctorEntity.class);
        DoctorEntity persisted = mock(DoctorEntity.class);

        when(mapper.toEntity(eq(entrada), anySet())).thenReturn(entity);
        when(repository.save(entity)).thenReturn(persisted);
        when(mapper.toDomain(persisted)).thenReturn(medico(null));

        gateway.criarDoutor(entrada);

        verify(clinicRepository, never()).findAllById(any());
    }

    @Test
    void existePorCpfEExistePorCrmDelegamAoRepositorio() {
        when(repository.existsByCpf("123.456.789-00")).thenReturn(true);
        when(repository.existsByCrm("CRM-SP-1")).thenReturn(false);

        assertThat(gateway.existePorCpf("123.456.789-00")).isTrue();
        assertThat(gateway.existePorCrm("CRM-SP-1")).isFalse();
    }

    @Test
    void buscarDoutorRetornaVazioQuandoNaoEncontrado() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThat(gateway.buscarDoutor(99L)).isEmpty();
    }

    @Test
    void deveLancarErroENaoDeletarQuandoMedicoInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gateway.deleteDoutor(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Doctor not found");

        verify(repository, never()).delete(any());
    }

    @Test
    void deveDeletarEntidadeQuandoMedicoExiste() {
        DoctorEntity entity = mock(DoctorEntity.class);
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        gateway.deleteDoutor(1L);

        verify(repository).delete(entity);
    }
}
