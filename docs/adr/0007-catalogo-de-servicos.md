# ADR 0007 — Catálogo de serviços
**Status:** aceito

**Contexto:** o eletricista repete serviços (instalar tomada, trocar disjuntor) e quer partir de uma estimativa de horas já conhecida ao montar o preço (ADR 0005).

**Decisão:**
- `Servico(id, ownerId, nome, horasPadrao)`, imutável (record), criado por `Servico.novo(...)`.
- `nome` obrigatório, aparado, até 120 caracteres; único por dono, sem diferenciar maiúsculas de minúsculas.
- `horasPadrao > 0` (serviço com zero hora não faz sentido; orçamentos podem usar zero horas direto no cálculo), normalizadas para escala 2, `HALF_UP`.
- Sem descrição, categoria ou preço fixo por enquanto: o preço sempre sai do cálculo (ADR 0005), a partir das horas, materiais e despesas.
- Violação de invariante lança `CatalogoInvalidoException`; nome repetido lança `ServicoJaCadastradoException`.

**Consequências:** o serviço é só uma estimativa de horas reutilizável; a ligação com materiais (kit padrão por serviço) fica fora do escopo até haver necessidade real.
