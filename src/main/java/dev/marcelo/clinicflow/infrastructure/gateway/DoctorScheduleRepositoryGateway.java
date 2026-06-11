package dev.marcelo.clinicflow.infrastructure.gateway;

import dev.marcelo.clinicflow.core.entities.DoctorSchedule;
import dev.marcelo.clinicflow.core.exceptions.DoctorNotFoundException;
import dev.marcelo.clinicflow.core.gateway.DoctorScheduleGateway;
import dev.marcelo.clinicflow.infrastructure.mapper.DoctorScheduleEntityMapper;
import dev.marcelo.clinicflow.infrastructure.persistence.DoctorEntity;
import dev.marcelo.clinicflow.infrastructure.persistence.DoctorRepository;
import dev.marcelo.clinicflow.infrastructure.persistence.DoctorScheduleEntity;
import dev.marcelo.clinicflow.infrastructure.persistence.DoctorScheduleRepository;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.util.List;

@Component
public class DoctorScheduleRepositoryGateway implements DoctorScheduleGateway {

    private final DoctorScheduleRepository repository;
    private final DoctorRepository doctorRepository;
    private final DoctorScheduleEntityMapper mapper;

    public DoctorScheduleRepositoryGateway(DoctorScheduleRepository repository, DoctorRepository doctorRepository, DoctorScheduleEntityMapper mapper) {
        this.repository = repository;
        this.doctorRepository = doctorRepository;
        this.mapper = mapper;
    }

    @Override
    public DoctorSchedule salvar(DoctorSchedule schedule) {
        DoctorEntity doctor = doctorRepository.findById(schedule.doctorId())
                .orElseThrow(() -> new DoctorNotFoundException(schedule.doctorId()));
        DoctorScheduleEntity saved = repository.save(mapper.toEntity(schedule, doctor));
        return mapper.toDomain(saved);
    }

    @Override
    public List<DoctorSchedule> listarPorMedico(Long doctorId) {
        return repository.findByDoctor_Id(doctorId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<DoctorSchedule> listarPorMedicoEDia(Long doctorId, DayOfWeek dayOfWeek) {
        return repository.findByDoctor_IdAndDayOfWeek(doctorId, dayOfWeek).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deletar(Long agendaId) {
        repository.deleteById(agendaId);
    }
}
