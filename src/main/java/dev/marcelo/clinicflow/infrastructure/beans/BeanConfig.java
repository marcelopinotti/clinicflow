package dev.marcelo.clinicflow.infrastructure.beans;

import dev.marcelo.clinicflow.core.gateway.AppointmentGateway;
import dev.marcelo.clinicflow.core.gateway.ClinicGateway;
import dev.marcelo.clinicflow.core.gateway.DoctorGateway;
import dev.marcelo.clinicflow.core.gateway.DoctorScheduleGateway;
import dev.marcelo.clinicflow.core.gateway.PatientGateway;
import dev.marcelo.clinicflow.core.usecases.appointment.AgendarConsultaCase;
import dev.marcelo.clinicflow.core.usecases.appointment.AgendarConsultaCaseImpl;
import dev.marcelo.clinicflow.core.usecases.appointment.BuscarConsultaCase;
import dev.marcelo.clinicflow.core.usecases.appointment.BuscarConsultaCaseImpl;
import dev.marcelo.clinicflow.core.usecases.appointment.CancelarConsultaCase;
import dev.marcelo.clinicflow.core.usecases.appointment.CancelarConsultaCaseImpl;
import dev.marcelo.clinicflow.core.usecases.appointment.ConfirmarConsultaCase;
import dev.marcelo.clinicflow.core.usecases.appointment.ConfirmarConsultaCaseImpl;
import dev.marcelo.clinicflow.core.usecases.appointment.ListarConsultasPorMedicoCase;
import dev.marcelo.clinicflow.core.usecases.appointment.ListarConsultasPorMedicoCaseImpl;
import dev.marcelo.clinicflow.core.usecases.appointment.ListarConsultasPorPacienteCase;
import dev.marcelo.clinicflow.core.usecases.appointment.ListarConsultasPorPacienteCaseImpl;
import dev.marcelo.clinicflow.core.usecases.appointment.RealizarConsultaCase;
import dev.marcelo.clinicflow.core.usecases.appointment.RealizarConsultaCaseImpl;
import dev.marcelo.clinicflow.core.usecases.appointment.RegistrarNoShowCase;
import dev.marcelo.clinicflow.core.usecases.appointment.RegistrarNoShowCaseImpl;
import dev.marcelo.clinicflow.core.usecases.clinic.CriarClinicaCase;
import dev.marcelo.clinicflow.core.usecases.clinic.CriarClinicaCaseImpl;
import dev.marcelo.clinicflow.core.usecases.doctor.AtualizarMedicoCase;
import dev.marcelo.clinicflow.core.usecases.doctor.AtualizarMedicoCaseImpl;
import dev.marcelo.clinicflow.core.usecases.doctor.BuscarMedicoCase;
import dev.marcelo.clinicflow.core.usecases.doctor.BuscarMedicoCaseImpl;
import dev.marcelo.clinicflow.core.usecases.doctor.CriarMedicoCase;
import dev.marcelo.clinicflow.core.usecases.doctor.CriarMedicoCaseImpl;
import dev.marcelo.clinicflow.core.usecases.doctor.DefinirAgendaMedicoCase;
import dev.marcelo.clinicflow.core.usecases.doctor.DefinirAgendaMedicoCaseImpl;
import dev.marcelo.clinicflow.core.usecases.doctor.DeletarMedicoCase;
import dev.marcelo.clinicflow.core.usecases.doctor.DeletarMedicoCaseImpl;
import dev.marcelo.clinicflow.core.usecases.doctor.ListarAgendaMedicoCase;
import dev.marcelo.clinicflow.core.usecases.doctor.ListarAgendaMedicoCaseImpl;
import dev.marcelo.clinicflow.core.usecases.doctor.ListarMedicosCase;
import dev.marcelo.clinicflow.core.usecases.doctor.ListarMedicosCaseImpl;
import dev.marcelo.clinicflow.core.usecases.doctor.ListarSlotsLivresCase;
import dev.marcelo.clinicflow.core.usecases.doctor.ListarSlotsLivresCaseImpl;
import dev.marcelo.clinicflow.core.usecases.doctor.RemoverAgendaMedicoCase;
import dev.marcelo.clinicflow.core.usecases.doctor.RemoverAgendaMedicoCaseImpl;
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
    public DefinirAgendaMedicoCase definirAgendaMedico(DoctorScheduleGateway doctorScheduleGateway, DoctorGateway doctorGateway) {
        return new DefinirAgendaMedicoCaseImpl(doctorScheduleGateway, doctorGateway);
    }

    @Bean
    public ListarAgendaMedicoCase listarAgendaMedico(DoctorScheduleGateway doctorScheduleGateway) {
        return new ListarAgendaMedicoCaseImpl(doctorScheduleGateway);
    }

    @Bean
    public RemoverAgendaMedicoCase removerAgendaMedico(DoctorScheduleGateway doctorScheduleGateway) {
        return new RemoverAgendaMedicoCaseImpl(doctorScheduleGateway);
    }

    @Bean
    public ListarSlotsLivresCase listarSlotsLivres(DoctorGateway doctorGateway, DoctorScheduleGateway doctorScheduleGateway,
                                                   AppointmentGateway appointmentGateway) {
        return new ListarSlotsLivresCaseImpl(doctorGateway, doctorScheduleGateway, appointmentGateway);
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

    @Bean
    public CriarClinicaCase criarClinica(ClinicGateway clinicGateway) {
        return new CriarClinicaCaseImpl(clinicGateway);
    }

    @Bean
    public AgendarConsultaCase agendarConsulta(AppointmentGateway appointmentGateway, ClinicGateway clinicGateway,
                                               DoctorGateway doctorGateway, PatientGateway patientGateway) {
        return new AgendarConsultaCaseImpl(appointmentGateway, clinicGateway, doctorGateway, patientGateway);
    }

    @Bean
    public ConfirmarConsultaCase confirmarConsulta(AppointmentGateway appointmentGateway) {
        return new ConfirmarConsultaCaseImpl(appointmentGateway);
    }

    @Bean
    public CancelarConsultaCase cancelarConsulta(AppointmentGateway appointmentGateway) {
        return new CancelarConsultaCaseImpl(appointmentGateway);
    }

    @Bean
    public RealizarConsultaCase realizarConsulta(AppointmentGateway appointmentGateway) {
        return new RealizarConsultaCaseImpl(appointmentGateway);
    }

    @Bean
    public RegistrarNoShowCase registrarNoShow(AppointmentGateway appointmentGateway) {
        return new RegistrarNoShowCaseImpl(appointmentGateway);
    }

    @Bean
    public BuscarConsultaCase buscarConsulta(AppointmentGateway appointmentGateway) {
        return new BuscarConsultaCaseImpl(appointmentGateway);
    }

    @Bean
    public ListarConsultasPorMedicoCase listarConsultasPorMedico(AppointmentGateway appointmentGateway) {
        return new ListarConsultasPorMedicoCaseImpl(appointmentGateway);
    }

    @Bean
    public ListarConsultasPorPacienteCase listarConsultasPorPaciente(AppointmentGateway appointmentGateway) {
        return new ListarConsultasPorPacienteCaseImpl(appointmentGateway);
    }

}
