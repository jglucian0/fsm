# ADR 0011 — Núcleo do orçamento
**Status:** proposto (premissas a confirmar)

**Contexto:** o orçamento é o centro do produto: o eletricista monta itens, informa deslocamento e desconto, envia ao cliente e acompanha a aprovação. Esta ADR cobre só o `core` (Fase 3a); persistência, API e envio vêm depois.

**Decisão:**
- **Referências:** `Orcamento` guarda só `clienteId`, sem importar nada do módulo `clientes`.
- **Item (`ItemOrcamento`):** descrição (aparada, até 200), tipo (`SERVICO` ou `MATERIAL`), quantidade e valor unitário. É um **snapshot**: o valor é copiado no momento, e mudanças posteriores no catálogo não alteram orçamentos existentes. Quantidade e valor unitário em escala 2 (HALF_UP); quantidade > 0 após normalizar; valor unitário >= 0 (zero vale como cortesia).
- **Totais:** subtotal do item = quantidade x valor unitário, arredondado uma vez (HALF_UP). Subtotal do orçamento = soma dos subtotais. **Total = subtotal + deslocamento − desconto.**
- **Deslocamento:** valor fixo opcional (>= 0). Cálculo por distância fica para depois.
- **Desconto:** valor fixo opcional (>= 0), nunca maior que subtotal + deslocamento. A invariante vale no construtor, então remover item ou reduzir deslocamento que a quebre também é rejeitado.
- **Estados (`StatusOrcamento`):** `RASCUNHO` → `ENVIADO` (exige pelo menos um item) → `APROVADO` → `FINALIZADO`; `ENVIADO` → `REJEITADO`. `REJEITADO` e `FINALIZADO` são finais. `FINALIZADO` marca o serviço concluído; a conversão em OS e agenda entra na Fase 4.
- **Edição:** itens, deslocamento e desconto só mudam em `RASCUNHO`.
- **Imutabilidade:** `Orcamento` é um record; cada operação devolve um novo objeto. O construtor canônico também serve à reconstituição vinda do banco e valida as mesmas invariantes.
- **Erros:** `OrcamentoInvalidoException` (invariante de dados) e `StatusOrcamentoInvalidoException` (operação inválida para o status atual, citando o status). Ambos viram 422 na camada web.
- **Fora desta etapa:** validade do orçamento (entra com o PDF, Fase 3) e margem (já coberta pela precificação do catálogo).

**Consequências:** o `core` fica testável sem Spring nem banco. O glossário do CONTEXT.md deve incluir `FINALIZADO` entre os status do orçamento.
