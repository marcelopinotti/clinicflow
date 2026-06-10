package dev.marcelo.clinicflow.infrastructure.controller;

import dev.marcelo.clinicflow.core.entities.Appointment;
import dev.marcelo.clinicflow.core.usecases.appointment.AgendarConsultaCase;
import dev.marcelo.clinicflow.core.usecases.appointment.BuscarConsultaCase;
import dev.marcelo.clinicflow.core.usecases.appointment.CancelarConsultaCase;
import dev.marcelo.clinicflow.core.usecases.appointment.ConfirmarConsultaCase;
import dev.marcelo.clinicflow.core.usecases.appointment.ListarConsultasPorMedicoCase;
import dev.marcelo.clinicflow.core.usecases.appointment.ListarConsultasPorPacienteCase;
import dev.marcelo.clinicflow.core.usecases.appointment.RealizarConsultaCase;
import dev.marcelo.clinicflow.core.usecases.appointment.RegistrarNoShowCase;
import dev.marcelo.clinicflow.infrastructure.dtos.AppointmentRequest;
import dev.marcelo.clinicflow.infrastructure.dtos.AppointmentResponse;
import dev.marcelo.clinicflow.infrastructure.mapper.AppointmentMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/consultas")
public class AppointmentController {

    private final AppointmentMapper mapper;
    private final AgendarConsultaCase agendarConsultaCase;
    private final ConfirmarConsultaCase confirmarConsultaCase;
    private final CancelarConsultaCase cancelarConsultaCase;
    private final RealizarConsultaCase realizarConsultaCase;
    private final RegistrarNoShowCase registrarNoShowCase;
    private final BuscarConsultaCase buscarConsultaCase;
    private final ListarConsultasPorMedicoCase listarConsultasPorMedicoCase;
    private final ListarConsultasPorPacienteCase listarConsultasPorPacienteCase;

    public AppointmentController(AppointmentMapper mapper, AgendarConsultaCase agendarConsultaCase,
                                 ConfirmarConsultaCase confirmarConsultaCase, CancelarConsultaCase cancelarConsultaCase,
                                 RealizarConsultaCase realizarConsultaCase, RegistrarNoShowCase registrarNoShowCase,
                                 BuscarConsultaCase buscarConsultaCase,
                                 ListarConsultasPorMedicoCase listarConsultasPorMedicoCase,
                                 ListarConsultasPorPacienteCase listarConsultasPorPacienteCase) {
        this.mapper = mapper;
        this.agendarConsultaCase = agendarConsultaCase;
        this.confirmarConsultaCase = confirmarConsultaCase;
        this.cancelarConsultaCase = cancelarConsultaCase;
        this.realizarConsultaCase = realizarConsultaCase;
        this.registrarNoShowCase = registrarNoShowCase;
        this.buscarConsultaCase = buscarConsultaCase;
        this.listarConsultasPorMedicoCase = listarConsultasPorMedicoCase;
        this.listarConsultasPorPacienteCase = listarConsultasPorPacienteCase;
    }

    @PostMapping
    public ResponseEntity<AppointmentResponse> agendar(@RequestBody AppointmentRequest request) {
        Appointment appointment = agendarConsultaCase.execute(mapper.toEntity(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(appointment));
    }

    @PatchMapping("/{id}/confirmar")
    public AppointmentResponse confirmar(@PathVariable Long id) {
        return mapper.toResponse(confirmarConsultaCase.execute(id));
    }

    @PatchMapping("/{id}/cancelar")
    public AppointmentResponse cancelar(@PathVariable Long id) {
        return mapper.toResponse(cancelarConsultaCase.execute(id));
    }

    @PatchMapping("/{id}/realizar")
    public AppointmentResponse realizar(@PathVariable Long id) {
        return mapper.toResponse(realizarConsultaCase.execute(id));
    }

    @PatchMapping("/{id}/no-show")
    public AppointmentResponse registrarNoShow(@PathVariable Long id) {
        return mapper.toResponse(registrarNoShowCase.execute(id));
    }

    @GetMapping("/{id}")
    public AppointmentResponse buscar(@PathVariable Long id) {
        return mapper.toResponse(buscarConsultaCase.execute(id));
    }

    @GetMapping
    public List<AppointmentResponse> listar(@RequestParam(required = false) Long medicoId,
                                            @RequestParam(required = false) Long pacienteId) {
        List<Appointment> consultas;
        if (medicoId != null) {
            consultas = listarConsultasPorMedicoCase.execute(medicoId);
        } else if (pacienteId != null) {
            consultas = listarConsultasPorPacienteCase.execute(pacienteId);
        } else {
            throw new IllegalArgumentException("Informe medicoId ou pacienteId para listar as consultas");
        }
        return consultas.stream()
                .map(mapper::toResponse)
                .toList();
    }
}
