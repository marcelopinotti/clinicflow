package dev.marcelo.clinicflow.core.usecases.patient;

import dev.marcelo.clinicflow.core.gateway.PatientGateway;

public class DeletarPacienteCaseImpl implements DeletarPacienteCase {

    private final PatientGateway patientGateway;

    public DeletarPacienteCaseImpl(PatientGateway patientGateway) {
        this.patientGateway = patientGateway;
    }

    @Override
    public void execute(Long id) {
    }
}
