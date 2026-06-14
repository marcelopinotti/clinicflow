package dev.marcelo.clinicflow.infrastructure.controller;

import dev.marcelo.clinicflow.core.entities.Patient;
import dev.marcelo.clinicflow.core.enums.Gender;
import dev.marcelo.clinicflow.core.exceptions.PatientNotFoundException;
import dev.marcelo.clinicflow.core.usecases.patient.AtualizarPacienteCase;
import dev.marcelo.clinicflow.core.usecases.patient.BuscarPacienteCase;
import dev.marcelo.clinicflow.core.usecases.patient.CriarPacienteCase;
import dev.marcelo.clinicflow.core.usecases.patient.DeletarPacienteCase;
import dev.marcelo.clinicflow.core.usecases.patient.ListarPacientesCase;
import dev.marcelo.clinicflow.infrastructure.mapper.PatientMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PatientController.class)
@Import(PatientMapper.class)
class PatientControllerTest {

    // Gender sobrescreve toString(); com Jackson 3 o valor aceito/serializado é "Feminino".
    private static final String REQUEST_JSON =
            "{\"firstName\":\"Ana\",\"lastName\":\"Souza\",\"cpf\":\"123.456.789-00\","
                    + "\"email\":\"ana@b.com\",\"address\":\"Rua 1\",\"phone\":\"11999\","
                    + "\"age\":30,\"gender\":\"Feminino\"}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CriarPacienteCase criarPacienteCase;
    @MockitoBean
    private AtualizarPacienteCase atualizarPacienteCase;
    @MockitoBean
    private BuscarPacienteCase buscarPacienteCase;
    @MockitoBean
    private ListarPacientesCase listarPacientesCase;
    @MockitoBean
    private DeletarPacienteCase deletarPacienteCase;

    private Patient paciente() {
        return new Patient(1L, "Ana", "Souza", "123.456.789-00", "ana@b.com",
                "Rua 1", "11999", 30, Gender.FEMALE);
    }

    @Test
    void deveCriarPacienteERetornar201() throws Exception {
        when(criarPacienteCase.execute(any(Patient.class))).thenReturn(paciente());

        mockMvc.perform(post("/api/v1/patient/criar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.gender").value("Feminino"));
    }

    @Test
    void deveListarPacientes() throws Exception {
        when(listarPacientesCase.execute()).thenReturn(List.of(paciente()));

        mockMvc.perform(get("/api/v1/patient/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void deveBuscarPacientePorId() throws Exception {
        when(buscarPacienteCase.execute(1L)).thenReturn(Optional.of(paciente()));

        mockMvc.perform(get("/api/v1/patient/listar/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deveRetornar404QuandoPacienteNaoEncontrado() throws Exception {
        when(buscarPacienteCase.execute(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/patient/listar/99"))
                .andExpect(status().isNotFound());
    }
}
