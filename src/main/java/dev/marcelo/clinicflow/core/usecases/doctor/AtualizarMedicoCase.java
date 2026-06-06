package dev.marcelo.clinicflow.core.usecases.doctor;

import dev.marcelo.clinicflow.core.entities.Doctor;

public interface AtualizarMedicoCase {
    Doctor execute(Long id,Doctor doctor);
}
