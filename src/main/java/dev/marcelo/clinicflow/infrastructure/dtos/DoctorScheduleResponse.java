package dev.marcelo.clinicflow.infrastructure.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.marcelo.clinicflow.infrastructure.utils.DatePatterns;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record DoctorScheduleResponse(
        Long id,
        Long doctorId,
        DayOfWeek dayOfWeek,
        @JsonFormat(pattern = DatePatterns.TIME)
        LocalTime startTime,
        @JsonFormat(pattern = DatePatterns.TIME)
        LocalTime endTime,
        Integer slotMinutes
) {
}
