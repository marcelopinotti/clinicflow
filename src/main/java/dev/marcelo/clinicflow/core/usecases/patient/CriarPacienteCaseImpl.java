package dev.marcelo.clinicflow.core.usecases.patient;

import dev.marcelo.clinicflow.core.entities.Patient;
import dev.marcelo.clinicflow.core.gateway.PatientGateway;

public class CriarPacienteCaseImpl implements CriarPacienteCase {

    private final PatientGateway patientGateway;

    public CriarPacienteCaseImpl(PatientGateway patientGateway) {
        this.patientGateway = patientGateway;
    }

    @Override
    public Patient execute(Patient patient) {
        return patientGateway.criarPaciente(patient);
    }
}
