package dev.marcelo.clinicflow.core.usecases.doctor;

import dev.marcelo.clinicflow.core.entities.Doctor;
import dev.marcelo.clinicflow.core.gateway.DoctorGateway;

public class AtualizarMedicoCaseImpl implements AtualizarMedicoCase {

    private final DoctorGateway doctorGateway;

     public AtualizarMedicoCaseImpl(DoctorGateway doctorGateway) {
        this.doctorGateway = doctorGateway;
    }

    @Override
    public Doctor execute(Long id, Doctor doctor) {
        return doctorGateway.buscarDoutor(id).map(existingDoctor -> {

            Doctor doctorUpdate = new Doctor(
                    existingDoctor.id(),
                    doctor.firstName(),
                    doctor.lastName(),
                    doctor.cpf(),
                    doctor.email(),
                    doctor.address(),
                    doctor.phone(),
                    doctor.age(),
                    doctor.crm(),
                    doctor.gender(),
                    doctor.specialty()
            );

            return doctorGateway.atualizarDoutor(doctorUpdate);

        }).orElseThrow(() -> new RuntimeException("Médico não encontrado"));
    }
}
