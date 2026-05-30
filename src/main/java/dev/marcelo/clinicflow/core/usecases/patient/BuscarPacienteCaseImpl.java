package dev.marcelo.clinicflow.core.usecases.patient;

import dev.marcelo.clinicflow.core.entities.Patient;
import dev.marcelo.clinicflow.core.gateway.PatientGateway;

public class BuscarPacienteCaseImpl implements BuscarPacienteCase {

    private final PatientGateway patientGateway;

    public BuscarPacienteCaseImpl(PatientGateway patientGateway) {
        this.patientGateway = patientGateway;
    }

    @Override
    public Patient execute(Long id) {
        return null;
    }
}
