package dev.marcelo.clinicflow.infrastructure.controller;

import dev.marcelo.clinicflow.core.entities.Doctor;
import dev.marcelo.clinicflow.core.usecases.doctor.AtualizarMedicoCase;
import dev.marcelo.clinicflow.core.usecases.doctor.BuscarMedicoCase;
import dev.marcelo.clinicflow.core.usecases.doctor.CriarMedicoCase;
import dev.marcelo.clinicflow.core.usecases.doctor.DeletarMedicoCase;
import dev.marcelo.clinicflow.core.usecases.doctor.ListarMedicosCase;
import dev.marcelo.clinicflow.infrastructure.dtos.DoctorRequest;
import dev.marcelo.clinicflow.infrastructure.dtos.DoctorResponse;
import dev.marcelo.clinicflow.infrastructure.mapper.DoctorMapper;
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


@RequestMapping("api/v1/doctor")
@RestController
public class DoctorController {

    private final CriarMedicoCase criarMedicoCase;
    private final DoctorMapper mapper;
    private final ListarMedicosCase listarMedicosCase;
    private final BuscarMedicoCase buscarMedicoCase;
    private final DeletarMedicoCase deletarMedicoCase;
    private final AtualizarMedicoCase atualizarMedicoCase;

    public DoctorController(CriarMedicoCase criarMedicoCase, DoctorMapper mapper, ListarMedicosCase listarMedicosCase, BuscarMedicoCase buscarMedicoCase,
                            DeletarMedicoCase deletarMedicoCase, AtualizarMedicoCase atualizarMedicoCase) {
        this.criarMedicoCase = criarMedicoCase;
        this.mapper = mapper;
        this.listarMedicosCase = listarMedicosCase;
        this.buscarMedicoCase = buscarMedicoCase;
        this.deletarMedicoCase = deletarMedicoCase;
        this.atualizarMedicoCase = atualizarMedicoCase;
    }

    @PostMapping("/criar")
    public DoctorResponse criar(@RequestBody DoctorRequest request){
        Doctor doctor = criarMedicoCase.execute(mapper.toEntity(request));
        return mapper.toResponse(doctor);
    }

    @GetMapping("/listar")
    public List<DoctorResponse> listar(){
        return  listarMedicosCase.execute().stream().map(mapper::toResponse ).collect(Collectors.toList());
    }

    @GetMapping("/listar/{id}")
    public DoctorResponse buscar(@PathVariable Long id){
        return buscarMedicoCase.execute(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
    }

    @DeleteMapping("/deletar/{id}")
    public void deletar(@PathVariable Long id){
        deletarMedicoCase.execute(id);
    }

    @PutMapping("/atualizar/{id}")
    public DoctorResponse atualizar(@PathVariable Long id, @RequestBody DoctorRequest request){
        Doctor doctor = mapper.toEntity(request);
        Doctor medicoAtualizado = atualizarMedicoCase.execute(id, doctor);
        return mapper.toResponse(medicoAtualizado);
    }

}
