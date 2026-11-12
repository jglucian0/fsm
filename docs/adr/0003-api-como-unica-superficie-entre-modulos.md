# ADR 0003 — Pacote `api` é a única superfície entre módulos
**Status:** aceito

**Contexto:** na especificação inicial, `orcamento` dependia de `NotificacaoService`, de outro módulo, criando acoplamento.

**Decisão:** cada módulo expõe apenas `api/` (eventos de domínio e contratos). Outros módulos só dependem desse pacote.

**Consequências:** comunicação por eventos ou contratos explícitos; regra validada em `ArchitectureTest`.
