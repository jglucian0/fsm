package dev.jgluciano.fsm.modules.catalogo.infrastructure.web;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CalculoPrecoRequest(
        @NotNull @Digits(integer = 6, fraction = 2) BigDecimal horasEstimadas,
        @NotNull @Digits(integer = 12, fraction = 2) BigDecimal custoMateriais,
        @NotNull @Digits(integer = 12, fraction = 2) BigDecimal despesasExtras) {
}
