package dev.marcelo.clinicflow.core.usecases.clinic;

import dev.marcelo.clinicflow.core.entities.Clinic;

import java.util.List;

public interface ListarClinicasCase {
    List<Clinic> execute();
}
