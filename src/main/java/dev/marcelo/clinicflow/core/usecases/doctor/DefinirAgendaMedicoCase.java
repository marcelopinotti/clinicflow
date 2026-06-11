package dev.marcelo.clinicflow.core.usecases.doctor;

import dev.marcelo.clinicflow.core.entities.DoctorSchedule;

public interface DefinirAgendaMedicoCase {
    DoctorSchedule execute(DoctorSchedule schedule);
}
