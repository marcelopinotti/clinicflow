package dev.marcelo.clinicflow.core.usecases.doctor;

import dev.marcelo.clinicflow.core.gateway.DoctorScheduleGateway;

public class RemoverAgendaMedicoCaseImpl implements RemoverAgendaMedicoCase {

    private final DoctorScheduleGateway scheduleGateway;

    public RemoverAgendaMedicoCaseImpl(DoctorScheduleGateway scheduleGateway) {
        this.scheduleGateway = scheduleGateway;
    }

    @Override
    public void execute(Long agendaId) {
        scheduleGateway.deletar(agendaId);
    }
}
