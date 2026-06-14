package dev.marcelo.clinicflow.integration;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Base para testes de integração (issue #15): sobe um PostgreSQL real via
 * Testcontainers e expõe a conexão para o contexto Spring através de
 * {@link ServiceConnection}, que sobrescreve {@code spring.datasource.*}.
 *
 * <p>Com isso a stack completa (Flyway → JPA/Hibernate em modo {@code validate})
 * roda contra um banco efêmero, sem depender do {@code docker-compose.yml} de
 * desenvolvimento, tornando {@code ./mvnw verify} reprodutível.
 */
@Testcontainers
public abstract class AbstractIntegrationTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16-alpine");
}
