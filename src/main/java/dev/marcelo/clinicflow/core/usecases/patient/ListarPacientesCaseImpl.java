package dev.marcelo.clinicflow.core.usecases.patient;

import dev.marcelo.clinicflow.core.entities.Patient;
import dev.marcelo.clinicflow.core.gateway.PatientGateway;

import java.util.List;

public class ListarPacientesCaseImpl implements ListarPacientesCase {

    private final PatientGateway patientGateway;

     public ListarPacientesCaseImpl(PatientGateway patientGateway) {
        this.patientGateway = patientGateway;
    }

    @Override
    public List<Patient> execute() {
        return List.of();
    }
}
