package dev.marcelo.clinicflow.core.exceptions;

import java.time.LocalDateTime;

public class OutsideDoctorScheduleException extends RuntimeException {

    public OutsideDoctorScheduleException(Long doctorId, LocalDateTime scheduledAt) {
        super("O horário " + scheduledAt + " está fora da agenda de atendimento do médico " + doctorId);
    }
}
