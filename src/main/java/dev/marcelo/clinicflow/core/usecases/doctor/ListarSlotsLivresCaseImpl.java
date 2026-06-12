package dev.marcelo.clinicflow.core.usecases.doctor;

import dev.marcelo.clinicflow.core.entities.Appointment;
import dev.marcelo.clinicflow.core.entities.DoctorSchedule;
import dev.marcelo.clinicflow.core.enums.AppointmentStatus;
import dev.marcelo.clinicflow.core.exceptions.DoctorNotFoundException;
import dev.marcelo.clinicflow.core.gateway.AppointmentGateway;
import dev.marcelo.clinicflow.core.gateway.DoctorGateway;
import dev.marcelo.clinicflow.core.gateway.DoctorScheduleGateway;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ListarSlotsLivresCaseImpl implements ListarSlotsLivresCase {

    private static final Set<AppointmentStatus> STATUS_ATIVOS =
            Set.of(AppointmentStatus.AGENDADA, AppointmentStatus.CONFIRMADA);

    private final DoctorGateway doctorGateway;
    private final DoctorScheduleGateway scheduleGateway;
    private final AppointmentGateway appointmentGateway;

    public ListarSlotsLivresCaseImpl(DoctorGateway doctorGateway, DoctorScheduleGateway scheduleGateway,
                                     AppointmentGateway appointmentGateway) {
        this.doctorGateway = doctorGateway;
        this.scheduleGateway = scheduleGateway;
        this.appointmentGateway = appointmentGateway;
    }

    @Override
    public List<LocalTime> execute(Long doctorId, LocalDate data) {
        doctorGateway.buscarDoutor(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException(doctorId));

        DayOfWeek dayOfWeek = data.getDayOfWeek();
        List<DoctorSchedule> janelas = scheduleGateway.listarPorMedicoEDia(doctorId, dayOfWeek);
        if (janelas.isEmpty()) {
            return List.of();
        }

        Set<LocalTime> ocupados = horariosOcupados(doctorId, data);

        TreeSet<LocalTime> livres = new TreeSet<>();
        for (DoctorSchedule janela : janelas) {
            for (LocalTime slot : gerarSlots(janela)) {
                if (!ocupados.contains(slot)) {
                    livres.add(slot);
                }
            }
        }
        return List.copyOf(livres);
    }

    private Set<LocalTime> horariosOcupados(Long doctorId, LocalDate data) {
        TreeSet<LocalTime> ocupados = new TreeSet<>();
        for (Appointment consulta : appointmentGateway.listarPorMedicoEData(doctorId, data)) {
            if (STATUS_ATIVOS.contains(consulta.status())) {
                ocupados.add(consulta.scheduledAt().toLocalTime());
            }
        }
        return ocupados;
    }

    private List<LocalTime> gerarSlots(DoctorSchedule janela) {
        List<LocalTime> slots = new ArrayList<>();
        LocalTime slot = janela.startTime();
        while (!slot.plusMinutes(janela.slotMinutes()).isAfter(janela.endTime())) {
            slots.add(slot);
            slot = slot.plusMinutes(janela.slotMinutes());
        }
        return slots;
    }
}
