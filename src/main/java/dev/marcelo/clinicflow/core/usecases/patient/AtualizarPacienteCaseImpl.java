package dev.marcelo.clinicflow.core.usecases.patient;

import dev.marcelo.clinicflow.core.entities.Patient;
import dev.marcelo.clinicflow.core.gateway.PatientGateway;

public class AtualizarPacienteCaseImpl implements AtualizarPacienteCase {

    private final PatientGateway patientGateway;

    public AtualizarPacienteCaseImpl(PatientGateway patientGateway) {
        this.patientGateway = patientGateway;
    }

    @Override
    public Patient execute(Long id, Patient patient) {
        return patientGateway.buscarPaciente(id).map(existingPatient -> {;

            Patient patientUpdate = new Patient(
                    existingPatient.id(),
                    patient.firstName(),
                    patient.lastName(),
                    patient.cpf(),
                    patient.email(),
                    patient.address(),
                    patient.phone(),
                    patient.age(),
                    patient.gender()
            );
            return patientGateway.atualizarPaciente(patientUpdate);
    }).orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
    }
}
