package dev.marcelo.clinicflow.core.usecases.doctor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ListarSlotsLivresCase {

    List<LocalTime> execute(Long doctorId, LocalDate data);
}
