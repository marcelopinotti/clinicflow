package dev.marcelo.clinicflow.infrastructure.mapper;

import dev.marcelo.clinicflow.core.entities.DoctorSchedule;
import dev.marcelo.clinicflow.infrastructure.dtos.DoctorScheduleRequest;
import dev.marcelo.clinicflow.infrastructure.dtos.DoctorScheduleResponse;
import org.springframework.stereotype.Component;

@Component
public class DoctorScheduleMapper {

    public DoctorSchedule toDomain(Long doctorId, DoctorScheduleRequest request) {
        return new DoctorSchedule(
                null,
                doctorId,
                request.dayOfWeek(),
                request.startTime(),
                request.endTime(),
                request.slotMinutes()
        );
    }

    public DoctorScheduleResponse toResponse(DoctorSchedule schedule) {
        return new DoctorScheduleResponse(
                schedule.id(),
                schedule.doctorId(),
                schedule.dayOfWeek(),
                schedule.startTime(),
                schedule.endTime(),
                schedule.slotMinutes()
        );
    }
}
