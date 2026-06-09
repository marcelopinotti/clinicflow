package dev.marcelo.clinicflow.core.usecases.clinic;

import dev.marcelo.clinicflow.core.entities.Clinic;
import dev.marcelo.clinicflow.core.enums.ClinicStatus;
import dev.marcelo.clinicflow.core.enums.DoctorSpecialty;
import dev.marcelo.clinicflow.core.exceptions.ClinicAlreadyExistsException;
import dev.marcelo.clinicflow.core.gateway.ClinicGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CriarClinicaCaseImplTest {

    @Mock
    private ClinicGateway clinicGateway;

    @InjectMocks
    private CriarClinicaCaseImpl criarClinicaCase;

    private Clinic novaClinica() {
        return new Clinic(
                null,
                "Clínica Saúde Total",
                "12.345.678/0001-90",
                "Rua das Flores, 100",
                "11999998888",
                "contato@saudetotal.com",
                ClinicStatus.ACTIVE,
                Set.of(DoctorSpecialty.values()[0])
        );
    }

    @Test
    void deveCriarClinicaQuandoCnpjNaoExiste() {
        Clinic entrada = novaClinica();
        Clinic salva = new Clinic(
                1L,
                entrada.name(),
                entrada.cnpj(),
                entrada.address(),
                entrada.phone(),
                entrada.email(),
                entrada.status(),
                entrada.specialties()
        );

        when(clinicGateway.existePorCnpj(entrada.cnpj())).thenReturn(false);
        when(clinicGateway.criarClinica(entrada)).thenReturn(salva);

        Clinic resultado = criarClinicaCase.execute(entrada);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.cnpj()).isEqualTo(entrada.cnpj());
        verify(clinicGateway).criarClinica(entrada);
    }

    @Test
    void deveLancarExcecaoENaoSalvarQuandoCnpjJaExiste() {
        Clinic entrada = novaClinica();

        when(clinicGateway.existePorCnpj(entrada.cnpj())).thenReturn(true);

        assertThatThrownBy(() -> criarClinicaCase.execute(entrada))
                .isInstanceOf(ClinicAlreadyExistsException.class)
                .hasMessageContaining(entrada.cnpj());

        verify(clinicGateway, never()).criarClinica(any(Clinic.class));
    }
}
