package dev.marcelo.clinicflow.core.usecases.doctor;

import dev.marcelo.clinicflow.core.entities.Doctor;

import java.util.List;

public interface ListarMedicosCase {
    List<Doctor> execute();
}
