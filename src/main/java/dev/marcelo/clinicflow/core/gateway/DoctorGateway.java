package dev.marcelo.clinicflow.core.gateway;

import dev.marcelo.clinicflow.core.entities.Doctor;

import java.util.List;
import java.util.Optional;

public interface DoctorGateway {

    Doctor criarDoutor(Doctor doctor);

    List<Doctor> listarDoutor();

    Optional<Doctor> buscarDoutor(Long id);

    Doctor atualizarDoutor(Doctor doctor);

    void deleteDoutor(Long id);

}
