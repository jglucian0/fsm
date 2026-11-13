# ADR 0008 — Persistência e API REST do catálogo
**Status:** proposto (premissas a confirmar)

**Contexto:** o domínio do catálogo (ADR 0005, 0006, 0007) está pronto e testado sem banco. Falta persistir e expor por HTTP, sem violar as regras do ArchUnit.

**Decisão — persistência (`infrastructure.persistence`):**
- Flyway `V2`: tabelas `configuracao_precificacao` (PK = `owner_id`, uma por dono), `material` e `servico` (PK `id uuid`, `owner_id` obrigatório).
- Índice único `(owner_id, lower(nome))` em `material` e `servico`: o caso de uso dá a mensagem amigável e o banco garante contra corrida.
- Tipos: dinheiro `numeric(14,2)`, horas `numeric(8,2)`, percentuais `numeric(7,4)`; `unidade` como `text`. O DTO valida a precisão (`@Digits`) para o banco nunca arredondar em silêncio.
- Entidades JPA e MapStruct ficam só em `infrastructure.persistence`; adaptadores implementam as portas do `core`.
- `ConfiguracaoPrecificacaoRepository` ganha `salvar` (criar ou substituir); novos casos de uso `DefinirConfiguracaoPrecificacaoUseCase` e `ObterConfiguracaoPrecificacaoUseCase`.

**Decisão — API (`infrastructure.web`, prefixo `/api/v1`):**
- `POST/GET /materiais`, `POST/GET /servicos`, `PUT/GET /precificacao/configuracao`, `POST /precificacao/calculo`.
- Erros em `application/exceptionhandler` (RFC 9457, `problemdetails` já habilitado): Bean Validation 400, invariante de domínio 422, nome repetido 409, configuração inexistente 404.
- O `core` continua sem Spring: os casos de uso viram beans em `application/config` (`@Configuration`), que também habilita o scan de `modules` (entidades, repositórios, controllers).
- Dono (`ownerId`): propriedade `fsm.owner-id` (UUID fixo) até a fase de autenticação; trocar por usuário autenticado não muda o domínio.

**Consequências:** a troca para multiusuário (Spring Security) mexe só em como o `ownerId` chega ao controller. Custo com fração de centavo continua não representável (ADR 0006).
