package dev.jgluciano.fsm;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/** Smoke test: a aplicação sobe e as migrations Flyway aplicam num PostgreSQL real. */
@SpringBootTest
@Testcontainers
class ApplicationContextIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Test
    void contextoSobeEMigrationsAplicam() {
        // se o contexto subiu e o Flyway rodou, o teste passou
    }
}
