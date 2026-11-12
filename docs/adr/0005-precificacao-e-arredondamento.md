# ADR 0005 — Precificação por VHT e índice de markup; regras de arredondamento
**Status:** aceito (premissas de arredondamento a confirmar)

**Contexto:** a fórmula simples (custo / (1 - margem)) ignora custos indiretos e tributação, inviabilizando a sustentabilidade do negócio.

**Decisão — configuração do usuário (`ConfiguracaoPrecificacao`):**
- `VHT = (custos fixos mensais + pró-labore) / horas produtivas mensais`
- `Índice de markup = 1 - (impostos% + taxas financeiras% + margem de lucro%) / 100`

**Decisão — cálculo (`CalcularPrecoServicoUseCase`):**
- `Custo direto = horas estimadas x VHT + custo de materiais + despesas extras`
- `Preço final = custo direto / índice de markup`

**Invariantes:** horas produtivas > 0; custos, pró-labore, percentuais e entradas do cálculo >= 0; soma dos percentuais < 100 (índice > 0). Percentuais em pontos (6 = 6%).

**Arredondamento:** `BigDecimal`. Cálculo interno em escala 10, `HALF_UP`. Valores monetários expostos em escala 2, `HALF_UP`. Nenhum valor intermediário é arredondado para 2 casas antes do preço final (VHT e custo direto são arredondados só para exibição).

**Consequências:** evita erro acumulado (ex.: VHT 46,6666... x 3h = 140,00 e não 140,01). Materiais e despesas entram como totais já calculados; a composição por item (quantidade x preço) fica para a Fase 2.
