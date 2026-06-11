package dev.marcelo.clinicflow.infrastructure.mapper;

import dev.marcelo.clinicflow.core.entities.DoctorSchedule;
import dev.marcelo.clinicflow.infrastructure.persistence.DoctorEntity;
import dev.marcelo.clinicflow.infrastructure.persistence.DoctorScheduleEntity;
import org.springframework.stereotype.Component;

@Component
public class DoctorScheduleEntityMapper {

    public DoctorScheduleEntity toEntity(DoctorSchedule schedule, DoctorEntity doctor) {
        return new DoctorScheduleEntity(
                schedule.id(),
                doctor,
                schedule.dayOfWeek(),
                schedule.startTime(),
                schedule.endTime(),
                schedule.slotMinutes()
        );
    }

    public DoctorSchedule toDomain(DoctorScheduleEntity entity) {
        return new DoctorSchedule(
                entity.getId(),
                entity.getDoctor().getId(),
                entity.getDayOfWeek(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getSlotMinutes()
        );
    }
}
