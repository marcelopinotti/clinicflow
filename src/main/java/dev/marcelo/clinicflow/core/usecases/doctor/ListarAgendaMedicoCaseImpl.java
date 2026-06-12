package dev.marcelo.clinicflow.core.usecases.doctor;

import dev.marcelo.clinicflow.core.entities.DoctorSchedule;
import dev.marcelo.clinicflow.core.gateway.DoctorScheduleGateway;

import java.util.List;

public class ListarAgendaMedicoCaseImpl implements ListarAgendaMedicoCase {

    private final DoctorScheduleGateway scheduleGateway;

    public ListarAgendaMedicoCaseImpl(DoctorScheduleGateway scheduleGateway) {
        this.scheduleGateway = scheduleGateway;
    }

    @Override
    public List<DoctorSchedule> execute(Long doctorId) {
        return scheduleGateway.listarPorMedico(doctorId);
    }
}
