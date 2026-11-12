# ADR 0001 — Monólito modular em vez de microsserviços
**Status:** aceito

**Contexto:** app de uso pessoal, um desenvolvedor, necessidade de demonstrar design arquitetural.

**Decisão:** um único deployable dividido em módulos de domínio com fronteiras verificadas por ArchUnit.

**Consequências:** sem complexidade de rede distribuída; módulos podem ser extraídos depois se necessário.
