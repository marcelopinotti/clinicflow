package dev.marcelo.clinicflow.infrastructure.gateway;

import dev.marcelo.clinicflow.core.entities.Appointment;
import dev.marcelo.clinicflow.core.enums.AppointmentStatus;
import dev.marcelo.clinicflow.core.gateway.AppointmentGateway;
import dev.marcelo.clinicflow.infrastructure.mapper.AppointmentEntityMapper;
import dev.marcelo.clinicflow.infrastructure.persistence.AppointmentEntity;
import dev.marcelo.clinicflow.infrastructure.persistence.AppointmentRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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

    @Override
    public Appointment salvar(Appointment appointment) {
        AppointmentEntity entity = mapper.toEntity(appointment);
        AppointmentEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
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
    public List<Appointment> listarPorPaciente(Long patientId) {
        return repository.findByPatientId(patientId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existeConflitoDeHorario(Long doctorId, LocalDateTime scheduledAt) {
        return repository.existsByDoctorIdAndScheduledAtAndStatusNot(doctorId, scheduledAt, AppointmentStatus.CANCELADA);
    }
}
