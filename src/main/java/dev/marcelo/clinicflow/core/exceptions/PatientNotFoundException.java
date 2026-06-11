package dev.marcelo.clinicflow.core.exceptions;

public class PatientNotFoundException extends RuntimeException {

    public PatientNotFoundException(Long id) {
        super("Paciente não encontrado para o id: " + id);
    }
}
