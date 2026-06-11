package dev.marcelo.clinicflow.core.exceptions;

public class AppointmentNotFoundException extends RuntimeException {

    public AppointmentNotFoundException(Long id) {
        super("Consulta não encontrada para o id: " + id);
    }
}
