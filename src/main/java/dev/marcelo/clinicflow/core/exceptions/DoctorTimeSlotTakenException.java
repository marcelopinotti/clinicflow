package dev.marcelo.clinicflow.core.exceptions;

import java.time.LocalDateTime;

public class DoctorTimeSlotTakenException extends RuntimeException {

    public DoctorTimeSlotTakenException(Long doctorId, LocalDateTime scheduledAt) {
        super("O médico " + doctorId + " já possui uma consulta ativa no horário " + scheduledAt);
    }
}
