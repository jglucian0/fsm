# ADR 0002 — Clean Architecture com core em Java puro
**Status:** aceito

**Contexto:** acoplar regras de negócio a anotações de JPA/Spring fere o SRP e atrasa testes unitários.

**Decisão:** cada módulo tem `core` (model, ports, usecase) sem dependência de framework e `infrastructure` com os adapters.

**Consequências:** testes de domínio rápidos, com mocks das portas; custo extra de mapeamento (MapStruct).
