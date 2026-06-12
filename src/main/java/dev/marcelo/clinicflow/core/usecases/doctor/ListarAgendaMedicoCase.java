package dev.marcelo.clinicflow.core.usecases.doctor;

import dev.marcelo.clinicflow.core.entities.DoctorSchedule;

import java.util.List;

public interface ListarAgendaMedicoCase {
    List<DoctorSchedule> execute(Long doctorId);
}
