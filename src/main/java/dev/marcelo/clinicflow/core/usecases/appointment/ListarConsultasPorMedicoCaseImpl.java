package dev.marcelo.clinicflow.core.usecases.appointment;

import dev.marcelo.clinicflow.core.entities.Appointment;

import java.util.List;

public class ListarConsultasPorMedicoCaseImpl implements ListarConsultasPorMedicoCase {

    @Override
    public List<Appointment> execute(Long doctorId) {
        return List.of();
    }
}
