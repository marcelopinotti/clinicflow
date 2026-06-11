package dev.marcelo.clinicflow.infrastructure.mapper;

import dev.marcelo.clinicflow.core.entities.Appointment;
import dev.marcelo.clinicflow.infrastructure.persistence.AppointmentEntity;
import org.springframework.stereotype.Component;

@Component
public class AppointmentEntityMapper {

    private final ClinicEntityMapper clinicEntityMapper;
    private final DoctorEntityMapper doctorEntityMapper;
    private final PatientEntityMapper patientEntityMapper;

    public AppointmentEntityMapper(ClinicEntityMapper clinicEntityMapper, DoctorEntityMapper doctorEntityMapper,
                                   PatientEntityMapper patientEntityMapper) {
        this.clinicEntityMapper = clinicEntityMapper;
        this.doctorEntityMapper = doctorEntityMapper;
        this.patientEntityMapper = patientEntityMapper;
    }

    public AppointmentEntity toEntity(Appointment appointment) {
        return new AppointmentEntity(
                appointment.id(),
                clinicEntityMapper.toEntity(appointment.clinic()),
                doctorEntityMapper.toEntity(appointment.doctor()),
                patientEntityMapper.toEntity(appointment.patient()),
                appointment.scheduledAt(),
                appointment.status()
        );
    }

    public Appointment toDomain(AppointmentEntity entity) {
        return new Appointment(
                entity.getId(),
                clinicEntityMapper.toDomain(entity.getClinic()),
                doctorEntityMapper.toDomain(entity.getDoctor()),
                patientEntityMapper.toDomain(entity.getPatient()),
                entity.getScheduledAt(),
                entity.getStatus()
        );
    }
}
