package dev.marcelo.clinicflow.infrastructure.dtos;

import java.time.LocalDateTime;

public record ReagendarConsultaRequest(
        LocalDateTime scheduledAt
) {
}
