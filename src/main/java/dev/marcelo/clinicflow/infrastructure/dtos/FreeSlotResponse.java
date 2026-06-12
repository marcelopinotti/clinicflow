package dev.marcelo.clinicflow.infrastructure.dtos;

import java.time.LocalTime;

public record FreeSlotResponse(
        LocalTime horario
) {
}
