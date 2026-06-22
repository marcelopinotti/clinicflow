package dev.marcelo.clinicflow.infrastructure.persistence;

import dev.marcelo.clinicflow.core.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {

    List<AppointmentEntity> findByDoctorId(Long doctorId);

    List<AppointmentEntity> findByDoctorIdAndScheduledAtBetween(Long doctorId, LocalDateTime inicio, LocalDateTime fim);

    List<AppointmentEntity> findByPatientId(Long patientId);

    @Query("""
            SELECT COUNT(a) > 0 FROM AppointmentEntity a
            WHERE a.doctor.id = :doctorId
              AND a.status <> :status
              AND a.scheduledAt > :inicio
              AND a.scheduledAt < :fim
              AND (:ignorarConsultaId IS NULL OR a.id <> :ignorarConsultaId)
            """)
    boolean existeConflitoNoIntervalo(@Param("doctorId") Long doctorId,
                                      @Param("status") AppointmentStatus status,
                                      @Param("inicio") LocalDateTime inicio,
                                      @Param("fim") LocalDateTime fim,
                                      @Param("ignorarConsultaId") Long ignorarConsultaId);
}
