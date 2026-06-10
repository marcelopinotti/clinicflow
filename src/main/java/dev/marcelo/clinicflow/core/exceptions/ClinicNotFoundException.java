package dev.marcelo.clinicflow.core.exceptions;

public class ClinicNotFoundException extends RuntimeException {

    public ClinicNotFoundException(Long id) {
        super("Clínica não encontrada para o id: " + id);
    }
}
