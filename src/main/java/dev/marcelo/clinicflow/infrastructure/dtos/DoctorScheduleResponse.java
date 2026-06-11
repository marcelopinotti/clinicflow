package dev.marcelo.clinicflow.infrastructure.dtos;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record DoctorScheduleResponse(
        Long id,
        Long doctorId,
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime,
        Integer slotMinutes
) {
}
