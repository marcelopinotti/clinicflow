package dev.marcelo.clinicflow.core.exceptions;

import dev.marcelo.clinicflow.core.enums.AppointmentStatus;

public class InvalidAppointmentStatusTransitionException extends RuntimeException {

    public InvalidAppointmentStatusTransitionException(AppointmentStatus from, AppointmentStatus to) {
        super("Transição de status inválida: " + from + " -> " + to);
    }
}
