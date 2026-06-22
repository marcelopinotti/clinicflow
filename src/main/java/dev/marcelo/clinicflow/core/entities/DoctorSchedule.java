package dev.marcelo.clinicflow.core.entities;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public record DoctorSchedule(
        Long id,
        Long doctorId,
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime,
        Integer slotMinutes
) {

    public boolean ehInicioDeSlot(LocalTime horario) {
        for (LocalTime slot : slots()) {
            if (slot.equals(horario)) {
                return true;
            }
        }
        return false;
    }

    public boolean cobre(LocalTime horario) {
        return !horario.isBefore(startTime) && horario.isBefore(endTime);
    }

    public List<LocalTime> slots() {
        List<LocalTime> slots = new ArrayList<>();
        LocalTime slot = startTime;
        while (!slot.plusMinutes(slotMinutes).isAfter(endTime)) {
            slots.add(slot);
            slot = slot.plusMinutes(slotMinutes);
        }
        return slots;
    }
}
