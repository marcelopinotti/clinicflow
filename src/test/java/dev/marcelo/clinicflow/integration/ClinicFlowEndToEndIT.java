package dev.marcelo.clinicflow.integration;

import com.jayway.jsonpath.JsonPath;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.concurrent.ThreadLocalRandom;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Teste de integração ponta a ponta (issue #15) exercitando a stack real
 * (controller → use case → gateway → JPA/Hibernate → Flyway) contra um
 * PostgreSQL de verdade provido por Testcontainers (ver {@link AbstractIntegrationTest}).
 * Complementa os testes unitários (use cases), de gateway e de controller
 * ({@code @WebMvcTest}) já existentes.
 *
 * <p>Os identificadores únicos (CNPJ/CPF/CRM/telefone/email) são aleatorizados por
 * execução para que múltiplas rodadas contra o mesmo banco não colidam nas
 * constraints de unicidade reais.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ClinicFlowEndToEndIT extends AbstractIntegrationTest {

    private static final DateTimeFormatter DATE_TIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private MockMvc mockMvc;

    @Test
    void fluxoCompleto_cadastraClinicaMedicoAgendaPacienteEAgendaConsulta() throws Exception {
        // 1) Cadastra clínica
        long clinicId = criarClinica(clinicaJson(novoCnpj(), phone(), email("clinica")));

        // 2) Cadastra médico já associado à clínica (relacionamento N:N médico↔clínica)
        long doctorId = criarMedico(clinicId);

        // A associação clínica↔médico foi persistida e é refletida na resposta.
        mockMvc.perform(get("/api/v1/medicos/listar/{id}", doctorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clinicIds", Matchers.contains((int) clinicId)));

        // 3) Define a agenda do médico (segunda, 09:00–12:00, slots de 30min → 6 slots)
        criarAgenda(doctorId);

        // 4) Cadastra paciente
        long patientId = criarPaciente();

        // 5) Lista slots livres para a próxima segunda-feira → 6 slots, primeiro 09:00
        LocalDate proximaSegunda = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        mockMvc.perform(get("/api/v1/medicos/{id}/agenda/livres", doctorId)
                        .param("data", proximaSegunda.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6))
                .andExpect(jsonPath("$[0].horario").value("09:00:00"));

        // 6) Agenda uma consulta às 09:00 da próxima segunda
        String scheduledAt = proximaSegunda.atTime(9, 0).format(DATE_TIME);
        String consulta = """
                {"clinicId":%d,"doctorId":%d,"patientId":%d,"scheduledAt":"%s"}
                """.formatted(clinicId, doctorId, patientId, scheduledAt);

        mockMvc.perform(post("/api/v1/consultas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(consulta))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.clinicId").value((int) clinicId))
                .andExpect(jsonPath("$.doctorId").value((int) doctorId))
                .andExpect(jsonPath("$.patientId").value((int) patientId))
                .andExpect(jsonPath("$.status").value("AGENDADA"));

        // 7) O slot das 09:00 agora está ocupado → restam 5 slots, primeiro 09:30
        mockMvc.perform(get("/api/v1/medicos/{id}/agenda/livres", doctorId)
                        .param("data", proximaSegunda.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].horario").value("09:30:00"));
    }

    @Test
    void cnpjDuplicado_violaUnicidadeERetorna409() throws Exception {
        String cnpj = novoCnpj();

        // Primeiro cadastro persiste com sucesso.
        criarClinica(clinicaJson(cnpj, phone(), email("clinica")));

        // Segundo cadastro com o mesmo CNPJ deve conflitar (unicidade real no banco).
        mockMvc.perform(post("/api/v1/clinicas/criar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clinicaJson(cnpj, phone(), email("clinica"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Clínica já cadastrada"));
    }

    @Test
    void cpfEcrmDuplicados_doMedico_retornam409() throws Exception {
        String cpf = cpf();
        String crm = "CRM" + digits(6);

        // Médico base.
        mockMvc.perform(post("/api/v1/medicos/criar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(medicoJson(cpf, crm, phone(), email("medico"))))
                .andExpect(status().isCreated());

        // Mesmo CPF, demais campos únicos novos → 409 por CPF.
        mockMvc.perform(post("/api/v1/medicos/criar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(medicoJson(cpf, "CRM" + digits(6), phone(), email("medico"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Médico já cadastrado"));

        // Mesmo CRM, demais campos únicos novos → 409 por CRM.
        mockMvc.perform(post("/api/v1/medicos/criar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(medicoJson(cpf(), crm, phone(), email("medico"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Médico já cadastrado"));
    }

    // ----------------------------------------------------------------------
    // Helpers de criação que retornam o id gerado pelo banco.
    // ----------------------------------------------------------------------

    private long criarClinica(String payload) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/clinicas/criar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andReturn();
        return idDe(result);
    }

    private long criarMedico(long clinicId) throws Exception {
        String medico = medicoJson(cpf(), "CRM" + digits(6), phone(), email("medico"))
                .replace("\"clinicIds\":[]", "\"clinicIds\":[" + clinicId + "]");

        MvcResult result = mockMvc.perform(post("/api/v1/medicos/criar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(medico))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andReturn();
        return idDe(result);
    }

    private void criarAgenda(long doctorId) throws Exception {
        String agenda = """
                {"dayOfWeek":"MONDAY","startTime":"09:00:00","endTime":"12:00:00","slotMinutes":30}
                """;
        mockMvc.perform(post("/api/v1/medicos/{id}/agenda/criar", doctorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(agenda))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.doctorId").value((int) doctorId));
    }

    private long criarPaciente() throws Exception {
        String paciente = """
                {"firstName":"Joao","lastName":"Souza","cpf":"%s","email":"%s",
                 "address":"Rua das Flores, 50","phone":"%s","age":30,"gender":"Masculino"}
                """.formatted(cpf(), email("paciente"), phone());

        MvcResult result = mockMvc.perform(post("/api/v1/patient/criar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(paciente))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andReturn();
        return idDe(result);
    }

    // ----------------------------------------------------------------------
    // Builders de JSON (enums serializados via toString — "Masculino"/"Cardiologista").
    // ----------------------------------------------------------------------

    private String clinicaJson(String cnpj, String phone, String email) {
        return """
                {"name":"Clínica Saúde","cnpj":"%s","address":"Rua Principal, 1",
                 "phone":"%s","email":"%s","specialties":["Cardiologista"]}
                """.formatted(cnpj, phone, email);
    }

    private String medicoJson(String cpf, String crm, String phone, String email) {
        return """
                {"firstName":"Ana","lastName":"Silva","cpf":"%s","email":"%s",
                 "address":"Av. Central, 100","phone":"%s","age":42,"crm":"%s",
                 "gender":"Masculino","specialty":"Cardiologista","clinicIds":[]}
                """.formatted(cpf, email, phone, crm);
    }

    // ----------------------------------------------------------------------
    // Utilidades.
    // ----------------------------------------------------------------------

    private long idDe(MvcResult result) throws Exception {
        String body = result.getResponse().getContentAsString();
        return ((Number) JsonPath.read(body, "$.id")).longValue();
    }

    private static String token() {
        return Long.toString(ThreadLocalRandom.current().nextLong(100_000_000L, 999_999_999L));
    }

    private static String email(String prefixo) {
        return prefixo + "-" + token() + "@example.com";
    }

    private static String digits(int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            sb.append(ThreadLocalRandom.current().nextInt(10));
        }
        return sb.toString();
    }

    private static String cpf() {
        return digits(3) + "." + digits(3) + "." + digits(3) + "-" + digits(2);
    }

    private static String novoCnpj() {
        return digits(2) + "." + digits(3) + "." + digits(3) + "/" + digits(4) + "-" + digits(2);
    }

    private static String phone() {
        return "+5511" + digits(9);
    }
}
