package dev.marcelo.clinicflow.core.exceptions;

import java.time.LocalDateTime;

public class DoctorScheduleConflictException extends RuntimeException {

    public DoctorScheduleConflictException(Long doctorId, LocalDateTime scheduledAt) {
        super("O médico " + doctorId + " já possui uma consulta agendada para " + scheduledAt);
    }
}
