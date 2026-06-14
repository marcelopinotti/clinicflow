package dev.marcelo.clinicflow.infrastructure.gateway;

import dev.marcelo.clinicflow.core.entities.Clinic;
import dev.marcelo.clinicflow.core.enums.ClinicStatus;
import dev.marcelo.clinicflow.core.enums.DoctorSpecialty;
import dev.marcelo.clinicflow.infrastructure.mapper.ClinicEntityMapper;
import dev.marcelo.clinicflow.infrastructure.persistence.ClinicEntity;
import dev.marcelo.clinicflow.infrastructure.persistence.ClinicRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClinicRepositoryGatewayTest {

    @Mock
    private ClinicRepository repository;

    @Mock
    private ClinicEntityMapper mapper;

    @InjectMocks
    private ClinicRepositoryGateway gateway;

    private Clinic clinica(Long id) {
        return new Clinic(id, "Clínica Saúde", "12.345.678/0001-90", "Rua 1", "11999",
                "a@b.com", ClinicStatus.ACTIVE, Set.of(DoctorSpecialty.CARDIOLOGY));
    }

    @Test
    void deveMapearEntidadeESalvarAoCriarClinica() {
        Clinic entrada = clinica(null);
        Clinic salvaDominio = clinica(1L);
        ClinicEntity entity = mock(ClinicEntity.class);
        ClinicEntity persisted = mock(ClinicEntity.class);

        when(mapper.toEntity(entrada)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(persisted);
        when(mapper.toDomain(persisted)).thenReturn(salvaDominio);

        Clinic resultado = gateway.criarClinica(entrada);

        assertThat(resultado).isSameAs(salvaDominio);
        verify(repository).save(entity);
    }

    @Test
    void existePorCnpjDelegaAoRepositorio() {
        when(repository.existsByCnpj("12.345.678/0001-90")).thenReturn(true);

        assertThat(gateway.existePorCnpj("12.345.678/0001-90")).isTrue();
    }

    @Test
    void buscarClinicaRetornaDominioQuandoEncontrada() {
        ClinicEntity entity = mock(ClinicEntity.class);
        Clinic dominio = clinica(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(dominio);

        assertThat(gateway.buscarClinica(1L)).contains(dominio);
    }

    @Test
    void buscarClinicaRetornaVazioQuandoNaoEncontrada() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThat(gateway.buscarClinica(99L)).isEmpty();
    }
}
