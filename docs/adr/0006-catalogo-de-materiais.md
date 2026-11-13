# ADR 0006 — Catálogo de materiais
**Status:** proposto (premissas a confirmar)

**Contexto:** o orçamento precisa de materiais com custo unitário para compor o custo direto (ADR 0005). Na Fase 1 o catálogo é independente do orçamento; a composição por item (quantidade x custo unitário) fica para a Fase 2.

**Decisão:**
- `Material(id, ownerId, nome, unidade, custoUnitario)`, imutável (record), criado por `Material.novo(...)`.
- `nome` e `unidade` obrigatórios, aparados; `nome` com até 120 caracteres.
- `custoUnitario >= 0`, normalizado para escala 2, `HALF_UP` (mesma regra de dinheiro do ADR 0005).
- `unidade` é texto livre (m, un, rolo...). Um enum fica para quando houver necessidade real.
- Nome único por dono, sem diferenciar maiúsculas de minúsculas (garantido no caso de uso e, depois, por índice único no Flyway).
- Violação de invariante lança `CatalogoInvalidoException`; nome repetido lança `MaterialJaCadastradoException`.

**Consequências:** custo com fração de centavo (ex.: 0,125 por metro) não é representável; se virar necessidade, o custo unitário ganha escala maior e o arredondamento passa para o item.
