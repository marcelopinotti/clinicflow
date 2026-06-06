//package dev.marcelo.clinicflow.infrastructure.controller;
//
//import dev.marcelo.clinicflow.core.entities.Clinic;
//import dev.marcelo.clinicflow.core.usecases.clinic.CriarClinicaCase;
//import dev.marcelo.clinicflow.infrastructure.dtos.ClinicRequest;
//import dev.marcelo.clinicflow.infrastructure.dtos.ClinicResponse;
//import dev.marcelo.clinicflow.infrastructure.mapper.ClinicMapper;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("api/v1/clinicas")
//public class ClinicController {
//
//    private final CriarClinicaCase criarClinicaCase;
//    private final ClinicMapper clinicMapper;
//
//    public ClinicController(CriarClinicaCase criarClinicaCase, ClinicMapper clinicMapper) {
//        this.criarClinicaCase = criarClinicaCase;
//        this.clinicMapper = clinicMapper;
//    }
//
//    @PostMapping("/criar")
//    public ClinicResponse criar(@RequestBody ClinicRequest request){
//        Clinic clinic = criarClinicaCase.execute(clinicMapper.toEntity(request));
//        return clinicMapper.toResponse(clinic);
//    }
//}
