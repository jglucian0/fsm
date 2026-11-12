# CONTEXT — FSM (cole este arquivo no início de uma conversa nova)

## Objetivo
App web (PWA no futuro) para uso pessoal de um eletricista autônomo, inspirado no "Meu Ajudante":
calculadora de preços, orçamento rápido, envio por WhatsApp/e-mail, agenda com lembretes, lista de materiais e recibos.
Projeto de portfólio de backend Java: o foco é arquitetura e qualidade de código.

## Stack
Java 21 (records, pattern matching, virtual threads) · Spring Boot 3.3.5 (Web, Data JPA, Validation, Actuator) ·
Maven · PostgreSQL 16 · Flyway · MapStruct 1.5.5 · Springdoc 2.6 · JUnit 5, Mockito, AssertJ, Testcontainers, ArchUnit ·
Docker Compose · GitHub Actions. RabbitMQ entra só na Fase 7. IDE: IntelliJ IDEA.

## Módulos (pacote base `dev.jgluciano.fsm.modules.<modulo>`)
| Módulo | Responsabilidade |
|---|---|
| catalogo | materiais (custo), serviços (homem-hora), calculadora de preços |
| clientes | cadastro de clientes |
| orcamento | orçamento, itens, margem, taxa de deslocamento, status (RASCUNHO, ENVIADO, APROVADO, REJEITADO) |
| agenda | conversão em OS, anti double-booking, atrasos, lembretes |
| documentos | PDF, e-mail, link WhatsApp, recibos (assíncrono via RabbitMQ na Fase 7) |

Estrutura de cada módulo: `api/` · `core/{model,ports,usecase}` · `infrastructure/{persistence,web,...}`.

## Regras de arquitetura (validadas por `ArchitectureTest`)
1. `core` é Java puro: sem Spring, JPA ou `infrastructure`.
2. `core.model` não depende de `usecase` nem de `ports`.
3. `infrastructure.web` não acessa `infrastructure.persistence` (passa pelos casos de uso).
4. Módulos só se acessam pelo pacote `api` (eventos e contratos), nunca por `core`/`infrastructure` do outro.
5. Entidades JPA ficam só em `infrastructure.persistence`; o domínio é mapeado por MapStruct.
6. Schema só muda via Flyway (`ddl-auto: validate`). Toda tabela tem `owner_id` (preparado para multiusuário).

## Convenções
- TDD: commit do teste falhando (Red) → implementação mínima (Green) → refatoração (Refactor).
- Commits no padrão Conventional Commits (`feat:`, `test:`, `refactor:`, `chore:`, `docs:`).
- Testes `*Test` = unitário/arquitetura (surefire); `*IT` = integração com Testcontainers (failsafe).
- Código e nomes de domínio em português (Orcamento, ItemOrcamento); termos técnicos em inglês.
- Sem Lombok: records para DTOs e valor.
- Dinheiro: `BigDecimal` com escala 2 e `RoundingMode.HALF_UP` (a definir no ADR da Fase 1).

## Estado atual
- Fase: **0 (Fundação)**, esqueleto gerado, aguardando validação local.
- Pronto: pom, docker-compose, Flyway V1 (baseline), esqueleto de pacotes, ArchUnit, smoke IT, CI.
- Próximo: Fase 1, `catalogo` + calculadora de preços (primeiro ciclo TDD).

## Decisões em aberto
- Frontend (PWA): tecnologia a definir após o backend.
- Autenticação (Spring Security): começar com usuário único, Fase 2 ou 3.
- Fórmula exata da calculadora de preços (custo/hora, margem, deslocamento): definir no início da Fase 1.
- Biblioteca de PDF (OpenPDF, PDFBox ou iText): Fase 3.
