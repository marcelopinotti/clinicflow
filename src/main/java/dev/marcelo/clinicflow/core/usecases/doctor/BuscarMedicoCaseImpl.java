package dev.marcelo.clinicflow.core.usecases.doctor;

import dev.marcelo.clinicflow.core.entities.Doctor;
import dev.marcelo.clinicflow.core.gateway.DoctorGateway;

import javax.print.Doc;
import java.util.List;
import java.util.Optional;

public class BuscarMedicoCaseImpl implements BuscarMedicoCase {

    private final DoctorGateway doctorGateway;

    public BuscarMedicoCaseImpl(DoctorGateway doctorGateway) {
        this.doctorGateway = doctorGateway;
    }

    @Override
    public Optional<Doctor> execute(long id) {
        return doctorGateway.buscarDoutor(id);
    }
}
