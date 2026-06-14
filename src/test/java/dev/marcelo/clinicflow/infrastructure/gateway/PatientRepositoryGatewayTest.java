package dev.marcelo.clinicflow.infrastructure.gateway;

import dev.marcelo.clinicflow.core.entities.Patient;
import dev.marcelo.clinicflow.core.enums.Gender;
import dev.marcelo.clinicflow.core.exceptions.PatientNotFoundException;
import dev.marcelo.clinicflow.infrastructure.mapper.PatientEntityMapper;
import dev.marcelo.clinicflow.infrastructure.persistence.PatientEntity;
import dev.marcelo.clinicflow.infrastructure.persistence.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientRepositoryGatewayTest {

    @Mock
    private PatientRepository repository;

    @Mock
    private PatientEntityMapper mapper;

    @InjectMocks
    private PatientRepositoryGateway gateway;

    private Patient paciente(Long id) {
        return new Patient(id, "Ana", "Souza", "123.456.789-00", "ana@b.com",
                "Rua 1", "11999", 30, Gender.FEMALE);
    }

    @Test
    void deveMapearEntidadeESalvarAoCriarPaciente() {
        Patient entrada = paciente(null);
        Patient salvoDominio = paciente(1L);
        PatientEntity entity = mock(PatientEntity.class);
        PatientEntity persisted = mock(PatientEntity.class);

        when(mapper.toEntity(entrada)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(persisted);
        when(mapper.toDomain(persisted)).thenReturn(salvoDominio);

        Patient resultado = gateway.criarPaciente(entrada);

        assertThat(resultado).isSameAs(salvoDominio);
        verify(repository).save(entity);
    }

    @Test
    void listarPacientesMapeiaTodasAsEntidades() {
        PatientEntity entity = mock(PatientEntity.class);
        Patient dominio = paciente(1L);
        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(dominio);

        assertThat(gateway.listarPacientes()).containsExactly(dominio);
    }

    @Test
    void buscarPacienteRetornaVazioQuandoNaoEncontrado() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThat(gateway.buscarPaciente(99L)).isEmpty();
    }

    @Test
    void deveLancarNotFoundENaoDeletarQuandoPacienteInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gateway.deletePaciente(99L))
                .isInstanceOf(PatientNotFoundException.class);

        verify(repository, never()).delete(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void deveDeletarEntidadeQuandoPacienteExiste() {
        PatientEntity entity = mock(PatientEntity.class);
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        gateway.deletePaciente(1L);

        verify(repository).delete(entity);
    }
}
