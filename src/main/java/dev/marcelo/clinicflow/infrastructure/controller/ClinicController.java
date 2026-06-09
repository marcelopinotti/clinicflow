package dev.marcelo.clinicflow.infrastructure.controller;

import dev.marcelo.clinicflow.core.entities.Clinic;
import dev.marcelo.clinicflow.core.usecases.clinic.CriarClinicaCase;
import dev.marcelo.clinicflow.infrastructure.dtos.ClinicRequest;
import dev.marcelo.clinicflow.infrastructure.dtos.ClinicResponse;
import dev.marcelo.clinicflow.infrastructure.mapper.ClinicMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/clinicas")
public class ClinicController {

    private final CriarClinicaCase criarClinicaCase;
    private final ClinicMapper mapper;

    public ClinicController(CriarClinicaCase criarClinicaCase, ClinicMapper mapper) {
        this.criarClinicaCase = criarClinicaCase;
        this.mapper = mapper;
    }

    @PostMapping("/criar")
    public ResponseEntity<ClinicResponse> criar(@RequestBody ClinicRequest request) {
        Clinic clinic = criarClinicaCase.execute(mapper.toEntity(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(clinic));
    }
}
