package dev.marcelo.clinicflow.infrastructure.controller;

import dev.marcelo.clinicflow.core.entities.Clinic;
import dev.marcelo.clinicflow.core.enums.ClinicStatus;
import dev.marcelo.clinicflow.core.enums.DoctorSpecialty;
import dev.marcelo.clinicflow.core.exceptions.ClinicAlreadyExistsException;
import dev.marcelo.clinicflow.core.usecases.clinic.CriarClinicaCase;
import dev.marcelo.clinicflow.infrastructure.mapper.ClinicMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClinicController.class)
@Import(ClinicMapper.class)
class ClinicControllerTest {

    // Jackson 3 (Spring Boot 4) (de)serializa enums via toString(); DoctorSpecialty
    // sobrescreve toString() com a descrição, então o valor aceito é "Cardiologista".
    private static final String REQUEST_JSON =
            "{\"name\":\"Clinica Saude\",\"cnpj\":\"12.345.678/0001-90\",\"address\":\"Rua 1\","
                    + "\"phone\":\"11999\",\"email\":\"a@b.com\",\"specialties\":[\"Cardiologista\"]}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CriarClinicaCase criarClinicaCase;

    private Clinic clinicaSalva() {
        return new Clinic(1L, "Clínica Saúde", "12.345.678/0001-90", "Rua 1",
                "11999", "a@b.com", ClinicStatus.ACTIVE, Set.of(DoctorSpecialty.CARDIOLOGY));
    }

    @Test
    void deveCriarClinicaERetornar201() throws Exception {
        when(criarClinicaCase.execute(any(Clinic.class))).thenReturn(clinicaSalva());

        mockMvc.perform(post("/api/v1/clinicas/criar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void deveRetornar409QuandoCnpjJaExiste() throws Exception {
        when(criarClinicaCase.execute(any(Clinic.class)))
                .thenThrow(new ClinicAlreadyExistsException("12.345.678/0001-90"));

        mockMvc.perform(post("/api/v1/clinicas/criar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Clínica já cadastrada"));
    }
}
