package dev.marcelo.clinicflow.core.usecases.doctor;

import dev.marcelo.clinicflow.core.entities.Doctor;
import dev.marcelo.clinicflow.core.gateway.DoctorGateway;

public class CriarMedicoCaseImpl implements CriarMedicoCase {
    private final DoctorGateway doctorGateway;

    public CriarMedicoCaseImpl(DoctorGateway doctorGateway) {
        this.doctorGateway = doctorGateway;
    }

    @Override
    public Doctor execute(Doctor doctor) {
        return doctorGateway.criarDoutor(doctor);
    }
}
