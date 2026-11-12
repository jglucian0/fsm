# FSM — Field Service Management (eletricista)

Monólito modular em Java 21 + Spring Boot 3.3, Clean Architecture, TDD.
Contexto completo do projeto: [`docs/CONTEXT.md`](docs/CONTEXT.md) · Roadmap: [`docs/ROADMAP.md`](docs/ROADMAP.md)

## Rodando localmente

```bash
docker compose up -d          # PostgreSQL + Mailpit
mvn spring-boot:run           # sobe a API em http://localhost:8080
```

- Health: http://localhost:8080/actuator/health
- Swagger UI: http://localhost:8080/swagger-ui.html
- Mailpit (e-mails de teste): http://localhost:8025

## Testes

```bash
mvn test       # unitários + regras de arquitetura (ArchUnit)
mvn verify     # + integração (*IT) com Testcontainers (requer Docker)
```

Para gerar o Maven Wrapper (opcional): `mvn -N wrapper:wrapper`
