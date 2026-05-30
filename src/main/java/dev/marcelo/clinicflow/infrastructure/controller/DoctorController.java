package dev.marcelo.clinicflow.infrastructure.controller;

import dev.marcelo.clinicflow.core.entities.Doctor;
import dev.marcelo.clinicflow.core.usecases.doctor.CriarMedicoCase;
import dev.marcelo.clinicflow.infrastructure.dtos.DoctorRequest;
import dev.marcelo.clinicflow.infrastructure.dtos.DoctorResponse;
import dev.marcelo.clinicflow.infrastructure.mapper.DoctorMapper;
import dev.marcelo.clinicflow.infrastructure.persistence.DoctorRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("api/v1/doctor")
@RestController
public class DoctorController {

    private final CriarMedicoCase criarMedicoCase;
    private final DoctorMapper mapper;

    public DoctorController(CriarMedicoCase criarMedicoCase, DoctorMapper mapper) {
        this.criarMedicoCase = criarMedicoCase;
        this.mapper = mapper;
    }

    @PostMapping("/criar")
    public DoctorResponse criar(@RequestBody DoctorRequest request){
        Doctor doctor = criarMedicoCase.execute(mapper.toEntity(request));
        return mapper.toResponse(doctor);
    }

}
