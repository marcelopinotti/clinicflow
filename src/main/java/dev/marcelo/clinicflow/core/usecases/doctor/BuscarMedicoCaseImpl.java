package dev.marcelo.clinicflow.core.usecases.doctor;

import dev.marcelo.clinicflow.core.entities.Doctor;
import dev.marcelo.clinicflow.core.gateway.DoctorGateway;

public class BuscarMedicoCaseImpl implements BuscarMedicoCase {

    private final DoctorGateway doctorGateway;

    public BuscarMedicoCaseImpl(DoctorGateway doctorGateway) {
        this.doctorGateway = doctorGateway;
    }

    @Override
    public Doctor execute(Long id) {
        return null;
    }
}
