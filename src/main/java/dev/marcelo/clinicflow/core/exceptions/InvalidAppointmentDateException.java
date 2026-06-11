package dev.marcelo.clinicflow.core.exceptions;

import java.time.LocalDateTime;

public class InvalidAppointmentDateException extends RuntimeException {

    public InvalidAppointmentDateException(LocalDateTime scheduledAt) {
        super("A data da consulta deve ser no futuro: " + scheduledAt);
    }
}
