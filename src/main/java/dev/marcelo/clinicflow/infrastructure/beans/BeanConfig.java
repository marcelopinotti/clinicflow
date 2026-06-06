package dev.marcelo.clinicflow.infrastructure.beans;

import dev.marcelo.clinicflow.core.entities.Doctor;
import dev.marcelo.clinicflow.core.gateway.DoctorGateway;
import dev.marcelo.clinicflow.core.usecases.doctor.AtualizarMedicoCase;
import dev.marcelo.clinicflow.core.usecases.doctor.AtualizarMedicoCaseImpl;
import dev.marcelo.clinicflow.core.usecases.doctor.BuscarMedicoCase;
import dev.marcelo.clinicflow.core.usecases.doctor.BuscarMedicoCaseImpl;
import dev.marcelo.clinicflow.core.usecases.doctor.CriarMedicoCase;
import dev.marcelo.clinicflow.core.usecases.doctor.CriarMedicoCaseImpl;
import dev.marcelo.clinicflow.core.usecases.doctor.DeletarMedicoCase;
import dev.marcelo.clinicflow.core.usecases.doctor.DeletarMedicoCaseImpl;
import dev.marcelo.clinicflow.core.usecases.doctor.ListarMedicosCase;
import dev.marcelo.clinicflow.core.usecases.doctor.ListarMedicosCaseImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public CriarMedicoCase criarMedico(DoctorGateway doctorGateway) {
        return new CriarMedicoCaseImpl(doctorGateway);

    }

    @Bean
    public AtualizarMedicoCase atualizarMedico(DoctorGateway doctorGateway) {
        return new AtualizarMedicoCaseImpl(doctorGateway);
    }

    @Bean
    public ListarMedicosCase listarMedicos(DoctorGateway doctorGateway) {
        return new ListarMedicosCaseImpl(doctorGateway);
    }

    @Bean
    public BuscarMedicoCase buscarMedico(DoctorGateway doctorGateway) {
        return new BuscarMedicoCaseImpl(doctorGateway);
    }


    @Bean
    public DeletarMedicoCase deletarMedico(DoctorGateway doctorGateway) {
        return new DeletarMedicoCaseImpl(doctorGateway);
    }



}
