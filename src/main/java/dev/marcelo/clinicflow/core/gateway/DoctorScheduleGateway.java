package dev.marcelo.clinicflow.core.gateway;

import dev.marcelo.clinicflow.core.entities.DoctorSchedule;

import java.time.DayOfWeek;
import java.util.List;

public interface DoctorScheduleGateway {

    DoctorSchedule salvar(DoctorSchedule schedule);

    List<DoctorSchedule> listarPorMedico(Long doctorId);

    List<DoctorSchedule> listarPorMedicoEDia(Long doctorId, DayOfWeek dayOfWeek);

    void deletar(Long agendaId);
}
