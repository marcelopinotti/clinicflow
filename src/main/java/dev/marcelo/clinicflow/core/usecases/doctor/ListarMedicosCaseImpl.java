package dev.marcelo.clinicflow.core.usecases.doctor;

import dev.marcelo.clinicflow.core.entities.Doctor;
import dev.marcelo.clinicflow.core.gateway.DoctorGateway;

import java.util.List;

public class ListarMedicosCaseImpl implements ListarMedicosCase {

    private final DoctorGateway doctorGateway;

    public ListarMedicosCaseImpl(DoctorGateway doctorGateway) {
        this.doctorGateway = doctorGateway;
    }

    @Override
    public List<Doctor> execute() {
        return List.of();
    }
}
