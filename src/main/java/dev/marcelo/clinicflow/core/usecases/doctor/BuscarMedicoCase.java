package dev.marcelo.clinicflow.core.usecases.doctor;

import dev.marcelo.clinicflow.core.entities.Doctor;

import java.util.List;
import java.util.Optional;

public interface BuscarMedicoCase {
    Optional<Doctor> execute(long id);
}
