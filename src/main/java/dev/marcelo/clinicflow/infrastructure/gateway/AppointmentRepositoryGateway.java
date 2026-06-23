package dev.marcelo.clinicflow.infrastructure.gateway;

import dev.marcelo.clinicflow.core.entities.Appointment;
import dev.marcelo.clinicflow.core.enums.AppointmentStatus;
import dev.marcelo.clinicflow.core.exceptions.DoctorTimeSlotTakenException;
import dev.marcelo.clinicflow.core.gateway.AppointmentGateway;
import dev.marcelo.clinicflow.infrastructure.mapper.AppointmentEntityMapper;
import dev.marcelo.clinicflow.infrastructure.persistence.AppointmentEntity;
import dev.marcelo.clinicflow.infrastructure.persistence.AppointmentRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Component
public class AppointmentRepositoryGateway implements AppointmentGateway {

    private final AppointmentRepository repository;
    private final AppointmentEntityMapper mapper;

    public AppointmentRepositoryGateway(AppointmentRepository repository, AppointmentEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    private static final String SLOT_UNIQUE_INDEX = "uq_consultas_medico_slot_ativa";

    @Override
    public Appointment salvar(Appointment appointment) {
        AppointmentEntity entity = mapper.toEntity(appointment);
        try {
            AppointmentEntity saved = repository.saveAndFlush(entity);
            return mapper.toDomain(saved);
        } catch (DataIntegrityViolationException ex) {
            if (isSlotUniqueViolation(ex)) {
                throw new DoctorTimeSlotTakenException(appointment.doctor().id(), appointment.scheduledAt());
            }
            throw ex;
        }
    }

    private boolean isSlotUniqueViolation(DataIntegrityViolationException ex) {
        for (Throwable cause = ex; cause != null; cause = cause.getCause()) {
            String message = cause.getMessage();
            if (message != null && message.contains(SLOT_UNIQUE_INDEX)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Optional<Appointment> buscarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Appointment> listarPorMedico(Long doctorId) {
        return repository.findByDoctorId(doctorId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Appointment> listarPorMedicoEData(Long doctorId, LocalDate data) {
        LocalDateTime inicio = data.atStartOfDay();
        LocalDateTime fim = data.atTime(LocalTime.MAX);
        return repository.findByDoctorIdAndScheduledAtBetween(doctorId, inicio, fim).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Appointment> listarPorPaciente(Long patientId) {
        return repository.findByPatientId(patientId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existeConflitoNoIntervalo(Long doctorId, LocalDateTime inicio, LocalDateTime fim, Long ignorarConsultaId) {
        return repository.existeConflitoNoIntervalo(doctorId, AppointmentStatus.CANCELADA, inicio, fim, ignorarConsultaId);
    }
}
