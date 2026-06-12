package dev.marcelo.clinicflow.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;

public interface DoctorScheduleRepository extends JpaRepository<DoctorScheduleEntity, Long> {

    List<DoctorScheduleEntity> findByDoctor_Id(Long doctorId);

    List<DoctorScheduleEntity> findByDoctor_IdAndDayOfWeek(Long doctorId, DayOfWeek dayOfWeek);
}
