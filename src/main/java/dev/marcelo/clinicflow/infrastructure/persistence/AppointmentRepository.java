package dev.marcelo.clinicflow.infrastructure.persistence;

import dev.marcelo.clinicflow.core.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {

    List<AppointmentEntity> findByDoctorId(Long doctorId);

    List<AppointmentEntity> findByPatientId(Long patientId);

    boolean existsByDoctorIdAndScheduledAtAndStatusNot(Long doctorId, LocalDateTime scheduledAt, AppointmentStatus status);
}
