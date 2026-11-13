package dev.jgluciano.fsm.modules.catalogo.infrastructure.web;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/** Percentuais em pontos: 6 significa 6%. A precisão acompanha as colunas do banco (ADR 0008). */
public record ConfiguracaoPrecificacaoRequest(
        @NotNull @Digits(integer = 12, fraction = 2) BigDecimal custosFixosMensais,
        @NotNull @Digits(integer = 12, fraction = 2) BigDecimal proLabore,
        @NotNull @Digits(integer = 6, fraction = 2) BigDecimal horasProdutivasMensais,
        @NotNull @Digits(integer = 3, fraction = 4) BigDecimal impostosPercentual,
        @NotNull @Digits(integer = 3, fraction = 4) BigDecimal taxasFinanceirasPercentual,
        @NotNull @Digits(integer = 3, fraction = 4) BigDecimal margemLucroPercentual) {
}
