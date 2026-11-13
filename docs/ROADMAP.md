# ROADMAP

Regra: só avança para a próxima fase com testes verdes e commit estável.

- [X] **Fase 0 — Fundação:** repo, Docker Compose, Flyway, CI, ArchUnit, Testcontainers, ADRs
- [X] **Fase 1 — Catálogo e calculadora de preços** (primeiro ciclo TDD)
- [ ] **Fase 2 — Clientes e orçamentos** (itens, totais, deslocamento, máquina de estados)
  - [X] 2a — Clientes: domínio (`core`) — ADR 0009
  - [X] 2b — Clientes: persistência (V3) e API REST — ADR 0010
  - [X] 2c — Orçamento: núcleo do domínio (`core.model`: Orcamento, ItemOrcamento, totais, máquina de estados) — ADR 0011
  - [ ] 2d — Orçamento: casos de uso e portas (`core.usecase` e `core.ports`)
  - [ ] 2e — Orçamento: persistência (V4: `orcamento` e `item_orcamento`) e API REST
  - [ ] 2f — Fechamento da Fase 2 (validação do `clienteId` via `api` de clientes, revisão de ADRs e docs)
- [ ] **Fase 3 — Documentos** (PDF, link WhatsApp, e-mail síncrono atrás de porta; validade do orçamento entra aqui)
- [ ] **Fase 4 — Agenda e OS** (conversão de orçamento aprovado, anti double-booking com `tstzrange`)
- [ ] **Fase 5 — Lista de materiais e recibos**
- [ ] **Fase 6 — Lembretes (secretária virtual) e dashboard**
- [ ] **Fase 7 — RabbitMQ + Transactional Outbox, deploy, observabilidade**
