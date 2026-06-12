package dev.marcelo.clinicflow.infrastructure.controller.handler;

import dev.marcelo.clinicflow.core.exceptions.AppointmentNotFoundException;
import dev.marcelo.clinicflow.core.exceptions.AppointmentNotReschedulableException;
import dev.marcelo.clinicflow.core.exceptions.ClinicNotFoundException;
import dev.marcelo.clinicflow.core.exceptions.DoctorNotFoundException;
import dev.marcelo.clinicflow.core.exceptions.DoctorScheduleConflictException;
import dev.marcelo.clinicflow.core.exceptions.InvalidAppointmentDateException;
import dev.marcelo.clinicflow.core.exceptions.InvalidAppointmentStatusTransitionException;
import dev.marcelo.clinicflow.core.exceptions.PatientNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AppointmentExceptionHandler {

    @ExceptionHandler({
            AppointmentNotFoundException.class,
            ClinicNotFoundException.class,
            DoctorNotFoundException.class,
            PatientNotFoundException.class
    })
    public ProblemDetail handleNotFound(RuntimeException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Recurso não encontrado");
        return problemDetail;
    }

    @ExceptionHandler({
            DoctorScheduleConflictException.class,
            InvalidAppointmentStatusTransitionException.class,
            AppointmentNotReschedulableException.class
    })
    public ProblemDetail handleConflict(RuntimeException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problemDetail.setTitle("Conflito na operação da consulta");
        return problemDetail;
    }

    @ExceptionHandler({
            InvalidAppointmentDateException.class,
            IllegalArgumentException.class
    })
    public ProblemDetail handleBadRequest(RuntimeException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Requisição inválida");
        return problemDetail;
    }
}
