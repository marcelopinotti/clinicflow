package dev.marcelo.clinicflow.core.exceptions;

import dev.marcelo.clinicflow.core.enums.AppointmentStatus;

public class AppointmentNotReschedulableException extends RuntimeException {

    public AppointmentNotReschedulableException(AppointmentStatus status) {
        super("Não é possível reagendar uma consulta com status " + status);
    }
}
