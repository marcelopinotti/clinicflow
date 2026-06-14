package dev.marcelo.clinicflow.infrastructure.controller;

import dev.marcelo.clinicflow.core.entities.Doctor;
import dev.marcelo.clinicflow.core.entities.DoctorSchedule;
import dev.marcelo.clinicflow.core.enums.DoctorSpecialty;
import dev.marcelo.clinicflow.core.enums.Gender;
import dev.marcelo.clinicflow.core.exceptions.DoctorAlreadyExistsException;
import dev.marcelo.clinicflow.core.exceptions.DoctorNotFoundException;
import dev.marcelo.clinicflow.core.exceptions.InvalidScheduleWindowException;
import dev.marcelo.clinicflow.core.exceptions.OverlappingScheduleException;
import dev.marcelo.clinicflow.core.usecases.doctor.AtualizarMedicoCase;
import dev.marcelo.clinicflow.core.usecases.doctor.BuscarMedicoCase;
import dev.marcelo.clinicflow.core.usecases.doctor.CriarMedicoCase;
import dev.marcelo.clinicflow.core.usecases.doctor.DefinirAgendaMedicoCase;
import dev.marcelo.clinicflow.core.usecases.doctor.DeletarMedicoCase;
import dev.marcelo.clinicflow.core.usecases.doctor.ListarAgendaMedicoCase;
import dev.marcelo.clinicflow.core.usecases.doctor.ListarMedicosCase;
import dev.marcelo.clinicflow.core.usecases.doctor.ListarSlotsLivresCase;
import dev.marcelo.clinicflow.core.usecases.doctor.RemoverAgendaMedicoCase;
import dev.marcelo.clinicflow.infrastructure.mapper.DoctorMapper;
import dev.marcelo.clinicflow.infrastructure.mapper.DoctorScheduleMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DoctorController.class)
@Import({DoctorMapper.class, DoctorScheduleMapper.class})
class DoctorControllerTest {

    // Enums com toString() customizado: specialty -> "Cardiologista", gender -> "Masculino".
    private static final String DOCTOR_JSON =
            "{\"firstName\":\"Joao\",\"lastName\":\"Silva\",\"cpf\":\"111\",\"email\":\"j@b.com\","
                    + "\"address\":\"Rua 1\",\"phone\":\"119\",\"age\":40,\"crm\":\"CRM-1\","
                    + "\"gender\":\"Masculino\",\"specialty\":\"Cardiologista\",\"clinicIds\":[]}";

    private static final String SCHEDULE_JSON =
            "{\"dayOfWeek\":\"MONDAY\",\"startTime\":\"08:00:00\",\"endTime\":\"12:00:00\",\"slotMinutes\":30}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CriarMedicoCase criarMedicoCase;
    @MockitoBean
    private ListarMedicosCase listarMedicosCase;
    @MockitoBean
    private BuscarMedicoCase buscarMedicoCase;
    @MockitoBean
    private DeletarMedicoCase deletarMedicoCase;
    @MockitoBean
    private AtualizarMedicoCase atualizarMedicoCase;
    @MockitoBean
    private DefinirAgendaMedicoCase definirAgendaMedicoCase;
    @MockitoBean
    private ListarAgendaMedicoCase listarAgendaMedicoCase;
    @MockitoBean
    private RemoverAgendaMedicoCase removerAgendaMedicoCase;
    @MockitoBean
    private ListarSlotsLivresCase listarSlotsLivresCase;

    private Doctor medico() {
        return new Doctor(1L, "Joao", "Silva", "111", "j@b.com", "Rua 1", "119", 40,
                "CRM-1", Gender.MALE, DoctorSpecialty.CARDIOLOGY, Set.of());
    }

    private DoctorSchedule agenda() {
        return new DoctorSchedule(1L, 1L, DayOfWeek.MONDAY,
                LocalTime.of(8, 0), LocalTime.of(12, 0), 30);
    }

    @Test
    void deveCriarMedicoERetornar201() throws Exception {
        when(criarMedicoCase.execute(any(Doctor.class))).thenReturn(medico());

        mockMvc.perform(post("/api/v1/medicos/criar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DOCTOR_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.specialty").value("Cardiologista"))
                .andExpect(jsonPath("$.gender").value("Masculino"));
    }

    @Test
    void deveRetornar409QuandoCpfJaExiste() throws Exception {
        when(criarMedicoCase.execute(any(Doctor.class)))
                .thenThrow(DoctorAlreadyExistsException.porCpf("111"));

        mockMvc.perform(post("/api/v1/medicos/criar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DOCTOR_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Médico já cadastrado"));
    }

    @Test
    void deveListarMedicos() throws Exception {
        when(listarMedicosCase.execute()).thenReturn(List.of(medico()));

        mockMvc.perform(get("/api/v1/medicos/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void deveBuscarMedicoPorId() throws Exception {
        when(buscarMedicoCase.execute(1L)).thenReturn(Optional.of(medico()));

        mockMvc.perform(get("/api/v1/medicos/listar/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void buscarMedicoInexistentePropagaRuntimeExceptionNaoMapeada() {
        // O controller lança RuntimeException("Doctor not found"), que nenhum @ControllerAdvice
        // trata — diferente dos demais recursos que retornam 404. Comportamento documentado.
        when(buscarMedicoCase.execute(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mockMvc.perform(get("/api/v1/medicos/listar/99")))
                .hasMessageContaining("Doctor not found");
    }

    @Test
    void deveCriarAgendaERetornar201() throws Exception {
        when(definirAgendaMedicoCase.execute(any(DoctorSchedule.class))).thenReturn(agenda());

        mockMvc.perform(post("/api/v1/medicos/1/agenda/criar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SCHEDULE_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.doctorId").value(1));
    }

    @Test
    void deveRetornar409QuandoAgendaSobrepoe() throws Exception {
        when(definirAgendaMedicoCase.execute(any(DoctorSchedule.class)))
                .thenThrow(new OverlappingScheduleException(1L, DayOfWeek.MONDAY));

        mockMvc.perform(post("/api/v1/medicos/1/agenda/criar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SCHEDULE_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Janela de atendimento sobreposta"));
    }

    @Test
    void deveRetornar400QuandoJanelaInvalida() throws Exception {
        when(definirAgendaMedicoCase.execute(any(DoctorSchedule.class)))
                .thenThrow(new InvalidScheduleWindowException("startTime deve ser antes de endTime"));

        mockMvc.perform(post("/api/v1/medicos/1/agenda/criar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SCHEDULE_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Janela de atendimento inválida"));
    }

    @Test
    void deveListarSlotsLivres() throws Exception {
        when(listarSlotsLivresCase.execute(eq(1L), eq(LocalDate.of(2026, 6, 15))))
                .thenReturn(List.of(LocalTime.of(8, 0), LocalTime.of(8, 30)));

        mockMvc.perform(get("/api/v1/medicos/1/agenda/livres").param("data", "2026-06-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].horario").value("08:00:00"))
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void deveRetornar404QuandoMedicoInexistenteAoListarSlots() throws Exception {
        when(listarSlotsLivresCase.execute(eq(99L), any(LocalDate.class)))
                .thenThrow(new DoctorNotFoundException(99L));

        mockMvc.perform(get("/api/v1/medicos/99/agenda/livres").param("data", "2026-06-15"))
                .andExpect(status().isNotFound());
    }
}
