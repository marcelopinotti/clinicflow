package dev.marcelo.clinicflow.core.exceptions;

public class DoctorNotFoundException extends RuntimeException {

    public DoctorNotFoundException(Long id) {
        super("Médico não encontrado para o id: " + id);
    }
}
