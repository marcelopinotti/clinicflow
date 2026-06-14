package dev.marcelo.clinicflow.infrastructure.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.marcelo.clinicflow.infrastructure.utils.DatePatterns;

import java.time.LocalTime;

public record FreeSlotResponse(
        @JsonFormat(pattern = DatePatterns.TIME)
        LocalTime horario
) {
}
