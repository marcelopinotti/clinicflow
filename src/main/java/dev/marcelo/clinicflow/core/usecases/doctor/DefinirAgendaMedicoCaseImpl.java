package dev.marcelo.clinicflow.core.usecases.doctor;

import dev.marcelo.clinicflow.core.entities.DoctorSchedule;
import dev.marcelo.clinicflow.core.exceptions.DoctorNotFoundException;
import dev.marcelo.clinicflow.core.exceptions.InvalidScheduleWindowException;
import dev.marcelo.clinicflow.core.exceptions.OverlappingScheduleException;
import dev.marcelo.clinicflow.core.gateway.DoctorGateway;
import dev.marcelo.clinicflow.core.gateway.DoctorScheduleGateway;

import java.time.Duration;
import java.util.List;

public class DefinirAgendaMedicoCaseImpl implements DefinirAgendaMedicoCase {

    private final DoctorScheduleGateway scheduleGateway;
    private final DoctorGateway doctorGateway;

    public DefinirAgendaMedicoCaseImpl(DoctorScheduleGateway scheduleGateway, DoctorGateway doctorGateway) {
        this.scheduleGateway = scheduleGateway;
        this.doctorGateway = doctorGateway;
    }

    @Override
    public DoctorSchedule execute(DoctorSchedule schedule) {
        doctorGateway.buscarDoutor(schedule.doctorId())
                .orElseThrow(() -> new DoctorNotFoundException(schedule.doctorId()));

        validarJanela(schedule);
        validarSobreposicao(schedule);

        return scheduleGateway.salvar(schedule);
    }

    private void validarJanela(DoctorSchedule schedule) {
        if (schedule.startTime() == null || schedule.endTime() == null) {
            throw new InvalidScheduleWindowException("startTime e endTime são obrigatórios");
        }
        if (!schedule.startTime().isBefore(schedule.endTime())) {
            throw new InvalidScheduleWindowException("startTime deve ser anterior a endTime");
        }
        if (schedule.slotMinutes() == null || schedule.slotMinutes() <= 0) {
            throw new InvalidScheduleWindowException("slotMinutes deve ser maior que zero");
        }
        long janelaMinutos = Duration.between(schedule.startTime(), schedule.endTime()).toMinutes();
        if (janelaMinutos % schedule.slotMinutes() != 0) {
            throw new InvalidScheduleWindowException("slotMinutes deve dividir a janela de atendimento de forma exata");
        }
    }

    private void validarSobreposicao(DoctorSchedule schedule) {
        List<DoctorSchedule> existentes =
                scheduleGateway.listarPorMedicoEDia(schedule.doctorId(), schedule.dayOfWeek());
        boolean sobrepoe = existentes.stream().anyMatch(existente -> seSobrepoem(existente, schedule));
        if (sobrepoe) {
            throw new OverlappingScheduleException(schedule.doctorId(), schedule.dayOfWeek());
        }
    }

    private boolean seSobrepoem(DoctorSchedule a, DoctorSchedule b) {
        return a.startTime().isBefore(b.endTime()) && b.startTime().isBefore(a.endTime());
    }
}
