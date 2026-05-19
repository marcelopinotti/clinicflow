package dev.marcelo.clinicflow.core.usecases.doctor;

import dev.marcelo.clinicflow.core.entities.Doctor;

public interface CriarMedicoCase {
    Doctor execute(Doctor doctor);
}
