package dev.marcelo.clinicflow.core.usecases.doctor;

import dev.marcelo.clinicflow.core.entities.Doctor;
import dev.marcelo.clinicflow.core.exceptions.DoctorAlreadyExistsException;
import dev.marcelo.clinicflow.core.gateway.DoctorGateway;

public class CriarMedicoCaseImpl implements CriarMedicoCase {
    private final DoctorGateway doctorGateway;

    public CriarMedicoCaseImpl(DoctorGateway doctorGateway) {
        this.doctorGateway = doctorGateway;
    }

    @Override
    public Doctor execute(Doctor doctor) {
        if (doctorGateway.existePorCpf(doctor.cpf())) {
            throw DoctorAlreadyExistsException.porCpf(doctor.cpf());
        }
        if (doctorGateway.existePorCrm(doctor.crm())) {
            throw DoctorAlreadyExistsException.porCrm(doctor.crm());
        }
        return doctorGateway.criarDoutor(doctor);
    }
}
