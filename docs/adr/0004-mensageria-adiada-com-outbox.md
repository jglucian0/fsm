# ADR 0004 — RabbitMQ adiado para a Fase 7, com Transactional Outbox
**Status:** aceito

**Contexto:** envio de PDF/e-mail não deve bloquear threads do Tomcat, mas a mensageria não é necessária para o MVP pessoal.

**Decisão:** começar com um envio síncrono atrás da porta `EnviadorDocumento`; na Fase 7 trocar a implementação por publicação no RabbitMQ usando o padrão Transactional Outbox.

**Consequências:** o domínio não muda na troca; a evolução fica documentada no histórico do repositório.
