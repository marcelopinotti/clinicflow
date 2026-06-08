package dev.marcelo.clinicflow.core.usecases.patient;

import dev.marcelo.clinicflow.core.entities.Patient;
import dev.marcelo.clinicflow.core.gateway.PatientGateway;

import java.util.Optional;

public class BuscarPacienteCaseImpl implements BuscarPacienteCase {

    private final PatientGateway patientGateway;

    public BuscarPacienteCaseImpl(PatientGateway patientGateway) {
        this.patientGateway = patientGateway;
    }

    @Override
    public Optional<Patient> execute(Long id) {
        return patientGateway.buscarPaciente(id);
    }
}
