package dev.marcelo.clinicflow.core.usecases.doctor;

import dev.marcelo.clinicflow.core.entities.Doctor;
import dev.marcelo.clinicflow.core.gateway.DoctorGateway;

public class AtualizarMedicoCaseImpl implements AtualizarMedicoCase {

    private final DoctorGateway doctorGateway;

     public AtualizarMedicoCaseImpl(DoctorGateway doctorGateway) {
        this.doctorGateway = doctorGateway;
    }

    @Override
    public Doctor execute(Doctor doctor) {
        return null;
    }
}
