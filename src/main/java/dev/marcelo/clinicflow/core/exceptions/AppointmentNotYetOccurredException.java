package dev.marcelo.clinicflow.core.exceptions;

import dev.marcelo.clinicflow.core.enums.AppointmentStatus;

import java.time.LocalDateTime;

public class AppointmentNotYetOccurredException extends RuntimeException {

    public AppointmentNotYetOccurredException(AppointmentStatus target, LocalDateTime scheduledAt) {
        super("Não é possível registrar " + target + " antes do horário da consulta (" + scheduledAt + ")");
    }
}
