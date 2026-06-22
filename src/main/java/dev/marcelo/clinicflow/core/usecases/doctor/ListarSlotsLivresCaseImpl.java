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

        List<Intervalo> ocupados = intervalosOcupados(doctorId, data, janelas);

        TreeSet<LocalTime> livres = new TreeSet<>();
        for (DoctorSchedule janela : janelas) {
            int slotMinutes = janela.slotMinutes();
            for (LocalTime inicio : janela.slots()) {
                Intervalo slot = new Intervalo(inicio, inicio.plusMinutes(slotMinutes));
                boolean coberto = ocupados.stream().anyMatch(slot::intersecta);
                if (!coberto) {
                    livres.add(inicio);
                }
            }
        }
        return List.copyOf(livres);
    }

    private List<Intervalo> intervalosOcupados(Long doctorId, LocalDate data, List<DoctorSchedule> janelas) {
        List<Intervalo> intervalos = new ArrayList<>();
        for (Appointment consulta : appointmentGateway.listarPorMedicoEData(doctorId, data)) {
            if (!STATUS_ATIVOS.contains(consulta.status())) {
                continue;
            }
            LocalTime inicio = consulta.scheduledAt().toLocalTime();
            int duracao = duracaoMinutos(janelas, inicio);
            intervalos.add(new Intervalo(inicio, inicio.plusMinutes(duracao)));
        }
        return intervalos;
    }

    private int duracaoMinutos(List<DoctorSchedule> janelas, LocalTime inicio) {
        return janelas.stream()
                .filter(janela -> janela.cobre(inicio))
                .map(DoctorSchedule::slotMinutes)
                .findFirst()
                .orElseGet(() -> janelas.stream()
                        .map(DoctorSchedule::slotMinutes)
                        .min(Integer::compareTo)
                        .orElse(0));
    }


    private record Intervalo(LocalTime inicio, LocalTime fim) {
        boolean intersecta(Intervalo outro) {
            return inicio.isBefore(outro.fim) && outro.inicio.isBefore(fim);
        }
    }
}
