package dev.marcelo.clinicflow.core.gateway;

import dev.marcelo.clinicflow.core.entities.Appointment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentGateway {

    Appointment salvar(Appointment appointment);

    Optional<Appointment> buscarPorId(Long id);

    List<Appointment> listarPorMedico(Long doctorId);

    List<Appointment> listarPorPaciente(Long patientId);

    boolean existeConflitoDeHorario(Long doctorId, LocalDateTime scheduledAt);
}
