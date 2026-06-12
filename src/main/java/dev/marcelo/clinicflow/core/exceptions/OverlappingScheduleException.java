package dev.marcelo.clinicflow.core.exceptions;

import java.time.DayOfWeek;

public class OverlappingScheduleException extends RuntimeException {

    public OverlappingScheduleException(Long doctorId, DayOfWeek dayOfWeek) {
        super("O médico " + doctorId + " já possui uma janela de atendimento que se sobrepõe em " + dayOfWeek);
    }
}
