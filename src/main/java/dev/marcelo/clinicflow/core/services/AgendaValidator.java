package dev.marcelo.clinicflow.core.services;

import dev.marcelo.clinicflow.core.entities.DoctorSchedule;
import dev.marcelo.clinicflow.core.exceptions.DoctorTimeSlotTakenException;
import dev.marcelo.clinicflow.core.exceptions.OutsideDoctorScheduleException;
import dev.marcelo.clinicflow.core.gateway.AppointmentGateway;
import dev.marcelo.clinicflow.core.gateway.DoctorScheduleGateway;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


public class AgendaValidator {

    private final AppointmentGateway appointmentGateway;
    private final DoctorScheduleGateway scheduleGateway;

    public AgendaValidator(AppointmentGateway appointmentGateway, DoctorScheduleGateway scheduleGateway) {
        this.appointmentGateway = appointmentGateway;
        this.scheduleGateway = scheduleGateway;
    }


    public void validar(Long doctorId, LocalDateTime scheduledAt, Long ignorarAppointmentId) {
        DayOfWeek dayOfWeek = scheduledAt.getDayOfWeek();
        LocalTime horario = scheduledAt.toLocalTime();

        List<DoctorSchedule> janelas = scheduleGateway.listarPorMedicoEDia(doctorId, dayOfWeek);
        DoctorSchedule janelaDoSlot = janelas.stream()
                .filter(janela -> janela.ehInicioDeSlot(horario))
                .findFirst()
                .orElseThrow(() -> new OutsideDoctorScheduleException(doctorId, scheduledAt));


        int slotMinutes = janelaDoSlot.slotMinutes();
        LocalDateTime inicio = scheduledAt.minusMinutes(slotMinutes);
        LocalDateTime fim = scheduledAt.plusMinutes(slotMinutes);
        if (appointmentGateway.existeConflitoNoIntervalo(doctorId, inicio, fim, ignorarAppointmentId)) {
            throw new DoctorTimeSlotTakenException(doctorId, scheduledAt);
        }
    }
}
