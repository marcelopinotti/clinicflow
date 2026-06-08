package dev.marcelo.clinicflow.infrastructure.controller;

import dev.marcelo.clinicflow.core.entities.Patient;
import dev.marcelo.clinicflow.core.usecases.patient.AtualizarPacienteCase;
import dev.marcelo.clinicflow.core.usecases.patient.BuscarPacienteCase;
import dev.marcelo.clinicflow.core.usecases.patient.CriarPacienteCase;
import dev.marcelo.clinicflow.core.usecases.patient.DeletarPacienteCase;
import dev.marcelo.clinicflow.core.usecases.patient.ListarPacientesCase;
import dev.marcelo.clinicflow.infrastructure.dtos.PatientRequest;
import dev.marcelo.clinicflow.infrastructure.dtos.PatientResponse;
import dev.marcelo.clinicflow.infrastructure.mapper.PatientMapper;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("api/v1/patient")
@RestController
public class PatientController {

    private final PatientMapper mapper;
    private final CriarPacienteCase criarPacienteCase;
    private final AtualizarPacienteCase atualizarPacienteCase;
    private final BuscarPacienteCase buscarPacienteCase;
    private final ListarPacientesCase listarPacientesCase;
    private final DeletarPacienteCase deletarPacienteCase;

    public PatientController(PatientMapper mapper, CriarPacienteCase criarPacienteCase, AtualizarPacienteCase atualizarPacienteCase,
                             BuscarPacienteCase buscarPacienteCase, ListarPacientesCase listarPacientesCase, DeletarPacienteCase deletarPacienteCase) {
        this.mapper = mapper;
        this.criarPacienteCase = criarPacienteCase;
        this.atualizarPacienteCase = atualizarPacienteCase;
        this.buscarPacienteCase = buscarPacienteCase;
        this.listarPacientesCase = listarPacientesCase;
        this.deletarPacienteCase = deletarPacienteCase;
    }

    @PostMapping("/criar")
    public PatientResponse criarPaciente(@RequestBody PatientRequest request){
        Patient patient = criarPacienteCase.execute(mapper.toEntity(request));
        return mapper.toResponse(patient);
    }

    @GetMapping("/listar")
    public List<PatientResponse> listarPacientes(){
        return  listarPacientesCase.execute().stream().map(mapper::toResponse ).collect(Collectors.toList());
    }

    @GetMapping("/listar/{id}")
    public PatientResponse buscarPaciente(@PathVariable Long id){
        return buscarPacienteCase.execute(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
    }

    @PutMapping("/atualizar/{id}")
    public PatientResponse atualizarPaciente(@PathVariable Long id,@RequestBody PatientRequest request){
        Patient patient =mapper.toEntity(request);
        Patient patientAtualizado = atualizarPacienteCase.execute(id, patient);
        return mapper.toResponse(patientAtualizado);
    }

    @DeleteMapping("/deletar/{id}")
    public void deletarPaciente(@PathVariable Long id){
        deletarPacienteCase.execute(id);
}
}
