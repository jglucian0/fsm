# ADR 0010 — Persistência e API de clientes
**Status:** aceito

**Decisão — persistência (`clientes.infrastructure.persistence`):**
- Flyway `V3`: tabela `cliente` (`id uuid` PK, `owner_id` obrigatório, `nome varchar(120)`, `telefone varchar(11)`, `email varchar(254)`, `endereco varchar(255)`; opcionais aceitam nulo).
- Índice `(owner_id, lower(nome))` **não único** (só para listagem): nomes repetidos são permitidos (ADR 0009).
- Entidade JPA, MapStruct e adaptador implementando `ClienteRepository`, como no catálogo. Listagem em ordem alfabética de nome.

**Decisão — API (`clientes.infrastructure.web`):**
- `POST /api/v1/clientes` (201) e `GET /api/v1/clientes` (200). Sem busca ou filtro por enquanto; entra quando houver necessidade real (lista longa).
- Bean Validation: `nome` obrigatório (400); `email` com no máximo 254 caracteres (400, limite da coluna). Regras de formato de telefone e e-mail ficam no domínio (422), com a mensagem do `core`.
- `ClienteInvalidoException` entra no `ApiExceptionHandler` como 422.

**Decisão — fronteira entre módulos:** `clientes` não importa nada de `catalogo`, nem a classe `DonoAtual`. Cada módulo tem a sua, lendo a mesma propriedade `fsm.owner-id`. Quando entrar autenticação, um mecanismo único substitui as cópias.

**Consequências:** a duplicação de `DonoAtual` é pequena e temporária, e preserva a regra de que módulos só se acessam pela `api`.
