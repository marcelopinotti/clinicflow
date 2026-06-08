package dev.marcelo.clinicflow.infrastructure.beans;

import dev.marcelo.clinicflow.core.entities.Doctor;
import dev.marcelo.clinicflow.core.gateway.DoctorGateway;
import dev.marcelo.clinicflow.core.gateway.PatientGateway;
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
import dev.marcelo.clinicflow.core.usecases.patient.AtualizarPacienteCase;
import dev.marcelo.clinicflow.core.usecases.patient.AtualizarPacienteCaseImpl;
import dev.marcelo.clinicflow.core.usecases.patient.BuscarPacienteCase;
import dev.marcelo.clinicflow.core.usecases.patient.BuscarPacienteCaseImpl;
import dev.marcelo.clinicflow.core.usecases.patient.CriarPacienteCase;
import dev.marcelo.clinicflow.core.usecases.patient.CriarPacienteCaseImpl;
import dev.marcelo.clinicflow.core.usecases.patient.DeletarPacienteCase;
import dev.marcelo.clinicflow.core.usecases.patient.DeletarPacienteCaseImpl;
import dev.marcelo.clinicflow.core.usecases.patient.ListarPacientesCase;
import dev.marcelo.clinicflow.core.usecases.patient.ListarPacientesCaseImpl;
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

    @Bean
    public CriarPacienteCase criarPaciente(PatientGateway gateway){
        return new CriarPacienteCaseImpl(gateway);
    }

    @Bean
    public AtualizarPacienteCase atualizarPaciente(PatientGateway gateway) {
        return new AtualizarPacienteCaseImpl(gateway);
    }

    @Bean
    public ListarPacientesCase listarPacientes(PatientGateway gateway) {
        return new ListarPacientesCaseImpl(gateway);
    }

    @Bean
    public BuscarPacienteCase buscarPaciente(PatientGateway gateway) {
        return new BuscarPacienteCaseImpl(gateway);
    }


    @Bean
    public DeletarPacienteCase deletarPaciente(PatientGateway gateway) {
        return new DeletarPacienteCaseImpl(gateway);
    }

}
