package dev.marcelo.clinicflow.core.usecases.doctor;

import dev.marcelo.clinicflow.core.entities.Doctor;
import dev.marcelo.clinicflow.core.enums.DoctorSpecialty;
import dev.marcelo.clinicflow.core.enums.Gender;
import dev.marcelo.clinicflow.core.exceptions.DoctorAlreadyExistsException;
import dev.marcelo.clinicflow.core.gateway.DoctorGateway;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CriarMedicoCaseImplTest {

    @Mock
    private DoctorGateway doctorGateway;

    @InjectMocks
    private CriarMedicoCaseImpl criarMedicoCase;

    private Doctor novoMedico() {
        return new Doctor(
                null,
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

    @Test
    void deveCriarMedicoQuandoCpfECrmNaoExistem() {
        Doctor entrada = novoMedico();
        Doctor salvo = new Doctor(
                1L,
                entrada.firstName(),
                entrada.lastName(),
                entrada.cpf(),
                entrada.email(),
                entrada.address(),
                entrada.phone(),
                entrada.age(),
                entrada.crm(),
                entrada.gender(),
                entrada.specialty(),
                entrada.clinicIds()
        );

        when(doctorGateway.existePorCpf(entrada.cpf())).thenReturn(false);
        when(doctorGateway.existePorCrm(entrada.crm())).thenReturn(false);
        when(doctorGateway.criarDoutor(entrada)).thenReturn(salvo);

        Doctor resultado = criarMedicoCase.execute(entrada);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.cpf()).isEqualTo(entrada.cpf());
        assertThat(resultado.crm()).isEqualTo(entrada.crm());
        verify(doctorGateway).criarDoutor(entrada);
    }

    @Test
    void deveLancarExcecaoENaoSalvarQuandoCpfJaExiste() {
        Doctor entrada = novoMedico();

        when(doctorGateway.existePorCpf(entrada.cpf())).thenReturn(true);

        assertThatThrownBy(() -> criarMedicoCase.execute(entrada))
                .isInstanceOf(DoctorAlreadyExistsException.class)
                .hasMessageContaining(entrada.cpf());

        verify(doctorGateway, never()).criarDoutor(any(Doctor.class));
    }

    @Test
    void deveLancarExcecaoENaoSalvarQuandoCrmJaExiste() {
        Doctor entrada = novoMedico();

        when(doctorGateway.existePorCpf(entrada.cpf())).thenReturn(false);
        when(doctorGateway.existePorCrm(entrada.crm())).thenReturn(true);

        assertThatThrownBy(() -> criarMedicoCase.execute(entrada))
                .isInstanceOf(DoctorAlreadyExistsException.class)
                .hasMessageContaining(entrada.crm());

        verify(doctorGateway, never()).criarDoutor(any(Doctor.class));
    }
}
