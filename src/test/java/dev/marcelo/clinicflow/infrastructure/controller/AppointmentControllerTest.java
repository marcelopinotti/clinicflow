package dev.marcelo.clinicflow.infrastructure.controller;

import dev.marcelo.clinicflow.core.entities.Appointment;
import dev.marcelo.clinicflow.core.entities.Clinic;
import dev.marcelo.clinicflow.core.entities.Doctor;
import dev.marcelo.clinicflow.core.entities.Patient;
import dev.marcelo.clinicflow.core.enums.AppointmentStatus;
import dev.marcelo.clinicflow.core.exceptions.AppointmentNotFoundException;
import dev.marcelo.clinicflow.core.exceptions.ClinicNotFoundException;
import dev.marcelo.clinicflow.core.exceptions.DoctorScheduleConflictException;
import dev.marcelo.clinicflow.core.exceptions.InvalidAppointmentDateException;
import dev.marcelo.clinicflow.core.exceptions.InvalidAppointmentStatusTransitionException;
import dev.marcelo.clinicflow.core.usecases.appointment.AgendarConsultaCase;
import dev.marcelo.clinicflow.core.usecases.appointment.BuscarConsultaCase;
import dev.marcelo.clinicflow.core.usecases.appointment.CancelarConsultaCase;
import dev.marcelo.clinicflow.core.usecases.appointment.ConfirmarConsultaCase;
import dev.marcelo.clinicflow.core.usecases.appointment.ListarConsultasPorMedicoCase;
import dev.marcelo.clinicflow.core.usecases.appointment.ListarConsultasPorPacienteCase;
import dev.marcelo.clinicflow.core.usecases.appointment.ReagendarConsultaCase;
import dev.marcelo.clinicflow.core.usecases.appointment.RealizarConsultaCase;
import dev.marcelo.clinicflow.core.usecases.appointment.RegistrarNoShowCase;
import dev.marcelo.clinicflow.infrastructure.mapper.AppointmentMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AppointmentController.class)
@Import(AppointmentMapper.class)
class AppointmentControllerTest {

    private static final LocalDateTime QUANDO = LocalDateTime.of(2026, 6, 15, 9, 0);

    private static final String AGENDAR_JSON =
            "{\"clinicId\":1,\"doctorId\":2,\"patientId\":3,\"scheduledAt\":\"2026-06-15 09:00:00\"}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AgendarConsultaCase agendarConsultaCase;
    @MockitoBean
    private ConfirmarConsultaCase confirmarConsultaCase;
    @MockitoBean
    private CancelarConsultaCase cancelarConsultaCase;
    @MockitoBean
    private ReagendarConsultaCase reagendarConsultaCase;
    @MockitoBean
    private RealizarConsultaCase realizarConsultaCase;
    @MockitoBean
    private RegistrarNoShowCase registrarNoShowCase;
    @MockitoBean
    private BuscarConsultaCase buscarConsultaCase;
    @MockitoBean
    private ListarConsultasPorMedicoCase listarConsultasPorMedicoCase;
    @MockitoBean
    private ListarConsultasPorPacienteCase listarConsultasPorPacienteCase;

    private Appointment consulta(AppointmentStatus status) {
        return new Appointment(
                10L,
                new Clinic(1L, null, null, null, null, null, null, null),
                new Doctor(2L, null, null, null, null, null, null, null, null, null, null, null),
                new Patient(3L, null, null, null, null, null, null, null, null),
                QUANDO,
                status);
    }

    @Test
    void deveAgendarConsultaERetornar201() throws Exception {
        when(agendarConsultaCase.execute(any(Appointment.class)))
                .thenReturn(consulta(AppointmentStatus.AGENDADA));

        mockMvc.perform(post("/api/v1/consultas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(AGENDAR_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.doctorId").value(2))
                .andExpect(jsonPath("$.status").value("AGENDADA"));
    }

    @Test
    void deveRetornar400QuandoDataNoPassado() throws Exception {
        when(agendarConsultaCase.execute(any(Appointment.class)))
                .thenThrow(new InvalidAppointmentDateException(QUANDO));

        mockMvc.perform(post("/api/v1/consultas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(AGENDAR_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Requisição inválida"));
    }

    @Test
    void deveRetornar409QuandoMedicoOcupado() throws Exception {
        when(agendarConsultaCase.execute(any(Appointment.class)))
                .thenThrow(new DoctorScheduleConflictException(2L, QUANDO));

        mockMvc.perform(post("/api/v1/consultas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(AGENDAR_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflito na operação da consulta"));
    }

    @Test
    void deveRetornar404QuandoClinicaInexistente() throws Exception {
        when(agendarConsultaCase.execute(any(Appointment.class)))
                .thenThrow(new ClinicNotFoundException(1L));

        mockMvc.perform(post("/api/v1/consultas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(AGENDAR_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso não encontrado"));
    }

    @Test
    void deveConfirmarConsulta() throws Exception {
        when(confirmarConsultaCase.execute(10L)).thenReturn(consulta(AppointmentStatus.CONFIRMADA));

        mockMvc.perform(patch("/api/v1/consultas/10/confirmar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMADA"));
    }

    @Test
    void deveRetornar409QuandoTransicaoDeStatusInvalida() throws Exception {
        when(confirmarConsultaCase.execute(10L))
                .thenThrow(new InvalidAppointmentStatusTransitionException(
                        AppointmentStatus.REALIZADA, AppointmentStatus.CONFIRMADA));

        mockMvc.perform(patch("/api/v1/consultas/10/confirmar"))
                .andExpect(status().isConflict());
    }

    @Test
    void deveReagendarConsultaEDelegarNovaData() throws Exception {
        LocalDateTime nova = LocalDateTime.of(2026, 6, 16, 10, 0);
        when(reagendarConsultaCase.execute(eq(10L), any(LocalDateTime.class)))
                .thenReturn(consulta(AppointmentStatus.AGENDADA));

        mockMvc.perform(patch("/api/v1/consultas/10/reagendar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"scheduledAt\":\"2026-06-16 10:00:00\"}"))
                .andExpect(status().isOk());

        verify(reagendarConsultaCase).execute(10L, nova);
    }

    @Test
    void deveRealizarConsulta() throws Exception {
        when(realizarConsultaCase.execute(10L)).thenReturn(consulta(AppointmentStatus.REALIZADA));

        mockMvc.perform(patch("/api/v1/consultas/10/realizar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REALIZADA"));
    }

    @Test
    void deveRegistrarNoShow() throws Exception {
        when(registrarNoShowCase.execute(10L)).thenReturn(consulta(AppointmentStatus.NO_SHOW));

        mockMvc.perform(patch("/api/v1/consultas/10/no-show"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("NO_SHOW"));
    }

    @Test
    void deveCancelarConsulta() throws Exception {
        when(cancelarConsultaCase.execute(10L)).thenReturn(consulta(AppointmentStatus.CANCELADA));

        mockMvc.perform(patch("/api/v1/consultas/10/cancelar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELADA"));
    }

    @Test
    void deveBuscarConsultaPorId() throws Exception {
        when(buscarConsultaCase.execute(10L)).thenReturn(consulta(AppointmentStatus.AGENDADA));

        mockMvc.perform(get("/api/v1/consultas/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void deveRetornar404QuandoConsultaInexistente() throws Exception {
        when(buscarConsultaCase.execute(99L)).thenThrow(new AppointmentNotFoundException(99L));

        mockMvc.perform(get("/api/v1/consultas/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveListarConsultasPorMedico() throws Exception {
        when(listarConsultasPorMedicoCase.execute(2L))
                .thenReturn(List.of(consulta(AppointmentStatus.AGENDADA)));

        mockMvc.perform(get("/api/v1/consultas").param("medicoId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].doctorId").value(2));
    }

    @Test
    void deveListarConsultasPorPaciente() throws Exception {
        when(listarConsultasPorPacienteCase.execute(3L))
                .thenReturn(List.of(consulta(AppointmentStatus.AGENDADA)));

        mockMvc.perform(get("/api/v1/consultas").param("pacienteId", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].patientId").value(3));
    }

    @Test
    void deveRetornar400QuandoListarSemFiltro() throws Exception {
        mockMvc.perform(get("/api/v1/consultas"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Requisição inválida"));
    }
}
