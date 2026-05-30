package dev.marcelo.clinicflow.core.usecases.patient;

import dev.marcelo.clinicflow.core.entities.Patient;
import dev.marcelo.clinicflow.core.gateway.PatientGateway;

public class AtualizarPacienteCaseImpl implements AtualizarPacienteCase {

    private final PatientGateway patientGateway;

    public AtualizarPacienteCaseImpl(PatientGateway patientGateway) {
        this.patientGateway = patientGateway;
    }

    @Override
    public Patient execute(Patient patient) {
        return null;
    }
}
