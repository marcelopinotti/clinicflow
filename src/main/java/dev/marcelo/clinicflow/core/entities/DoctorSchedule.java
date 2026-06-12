package dev.marcelo.clinicflow.core.entities;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record DoctorSchedule(
        Long id,
        Long doctorId,
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime,
        Integer slotMinutes
) {
}
