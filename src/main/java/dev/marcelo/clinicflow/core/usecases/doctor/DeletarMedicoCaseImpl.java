package dev.marcelo.clinicflow.core.usecases.doctor;

import dev.marcelo.clinicflow.core.gateway.DoctorGateway;

public class DeletarMedicoCaseImpl implements DeletarMedicoCase {

    private final DoctorGateway doctorGateway;

    public DeletarMedicoCaseImpl(DoctorGateway doctorGateway) {
        this.doctorGateway = doctorGateway;
    }

    @Override
    public void execute(Long id) {
        doctorGateway.deleteDoutor(id);
    }
}
