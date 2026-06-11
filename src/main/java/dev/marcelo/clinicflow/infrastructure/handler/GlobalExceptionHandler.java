package dev.marcelo.clinicflow.infrastructure.controller.handler;

import dev.marcelo.clinicflow.core.exceptions.ClinicAlreadyExistsException;
import dev.marcelo.clinicflow.core.exceptions.InvalidScheduleWindowException;
import dev.marcelo.clinicflow.core.exceptions.OverlappingScheduleException;
import dev.marcelo.clinicflow.core.exceptions.PatientNotFoundException;
import dev.marcelo.clinicflow.core.exceptions.DoctorAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
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

    @ExceptionHandler(DoctorAlreadyExistsException.class)
    public ProblemDetail handleDoctorAlreadyExists(DoctorAlreadyExistsException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problemDetail.setTitle("Médico já cadastrado");
        return problemDetail;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleNotReadable(HttpMessageNotReadableException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Requisição inválida: verifique os campos e os valores de enum enviados.");
        problemDetail.setTitle("Requisição inválida");
        return problemDetail;
    }

    @ExceptionHandler(PatientNotFoundException.class)
    public ProblemDetail handlePatientNotFound(PatientNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Paciente não encontrado");
        return problemDetail;
    }

    @ExceptionHandler(OverlappingScheduleException.class)
    public ProblemDetail handleOverlappingSchedule(OverlappingScheduleException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problemDetail.setTitle("Janela de atendimento sobreposta");
        return problemDetail;
    }

    @ExceptionHandler(InvalidScheduleWindowException.class)
    public ProblemDetail handleInvalidScheduleWindow(InvalidScheduleWindowException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Janela de atendimento inválida");
        return problemDetail;
    }
}
