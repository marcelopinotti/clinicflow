package dev.marcelo.clinicflow.infrastructure.controller.handler;

import dev.marcelo.clinicflow.core.exceptions.ClinicAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ClinicAlreadyExistsException.class)
    public ProblemDetail handleClinicAlreadyExists(ClinicAlreadyExistsException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problemDetail.setTitle("Clínica já cadastrada");
        return problemDetail;
    }
}
