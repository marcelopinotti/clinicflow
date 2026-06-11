package dev.marcelo.clinicflow.core.exceptions;

public class PatientNotFoundException extends RuntimeException {

    public PatientNotFoundException(Long id) {
        super("Nenhum paciente encontrado com o id: " + id);
    }
}
