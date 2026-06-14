package dev.marcelo.clinicflow.infrastructure.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.marcelo.clinicflow.infrastructure.utils.DatePatterns;

import java.time.LocalDateTime;

public record ReagendarConsultaRequest(
        @JsonFormat(pattern = DatePatterns.DATE_TIME)
        LocalDateTime scheduledAt
) {
}
