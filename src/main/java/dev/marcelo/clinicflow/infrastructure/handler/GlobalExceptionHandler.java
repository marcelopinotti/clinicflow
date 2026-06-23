package dev.marcelo.clinicflow.infrastructure.handler;

import dev.marcelo.clinicflow.core.exceptions.AppointmentNotFoundException;
import dev.marcelo.clinicflow.core.exceptions.AppointmentNotReschedulableException;
import dev.marcelo.clinicflow.core.exceptions.AppointmentNotYetOccurredException;
import dev.marcelo.clinicflow.core.exceptions.ClinicAlreadyExistsException;
import dev.marcelo.clinicflow.core.exceptions.ClinicNotFoundException;
import dev.marcelo.clinicflow.core.exceptions.DoctorAlreadyExistsException;
import dev.marcelo.clinicflow.core.exceptions.DoctorNotAffiliatedToClinicException;
import dev.marcelo.clinicflow.core.exceptions.DoctorNotFoundException;
import dev.marcelo.clinicflow.core.exceptions.DoctorScheduleConflictException;
import dev.marcelo.clinicflow.core.exceptions.DoctorTimeSlotTakenException;
import dev.marcelo.clinicflow.core.exceptions.InvalidAppointmentDateException;
import dev.marcelo.clinicflow.core.exceptions.InvalidAppointmentStatusTransitionException;
import dev.marcelo.clinicflow.core.exceptions.InvalidScheduleWindowException;
import dev.marcelo.clinicflow.core.exceptions.OutsideDoctorScheduleException;
import dev.marcelo.clinicflow.core.exceptions.OverlappingScheduleException;
import dev.marcelo.clinicflow.core.exceptions.PatientNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            AppointmentNotFoundException.class,
            ClinicNotFoundException.class,
            DoctorNotFoundException.class
    })
    public ProblemDetail handleNotFound(RuntimeException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Recurso não encontrado");
        return problemDetail;
    }

    @ExceptionHandler(PatientNotFoundException.class)
    public ProblemDetail handlePatientNotFound(PatientNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Paciente não encontrado");
        return problemDetail;
    }

    @ExceptionHandler({
            DoctorScheduleConflictException.class,
            DoctorTimeSlotTakenException.class,
            DoctorNotAffiliatedToClinicException.class,
            InvalidAppointmentStatusTransitionException.class,
            AppointmentNotReschedulableException.class,
            AppointmentNotYetOccurredException.class
    })
    public ProblemDetail handleAppointmentConflict(RuntimeException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problemDetail.setTitle("Conflito na operação da consulta");
        return problemDetail;
    }

    @ExceptionHandler(OutsideDoctorScheduleException.class)
    public ProblemDetail handleOutsideDoctorSchedule(OutsideDoctorScheduleException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problemDetail.setTitle("Fora da agenda do médico");
        return problemDetail;
    }

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

    @ExceptionHandler(OverlappingScheduleException.class)
    public ProblemDetail handleOverlappingSchedule(OverlappingScheduleException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problemDetail.setTitle("Janela de atendimento sobreposta");
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

    @ExceptionHandler(InvalidScheduleWindowException.class)
    public ProblemDetail handleInvalidScheduleWindow(InvalidScheduleWindowException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Janela de atendimento inválida");
        return problemDetail;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, "A operação conflita com um registro já existente.");
        problemDetail.setTitle("Conflito de integridade");
        return problemDetail;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleNotReadable(HttpMessageNotReadableException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Requisição inválida: verifique os campos e os valores de enum enviados.");
        problemDetail.setTitle("Requisição inválida");
        return problemDetail;
    }
}