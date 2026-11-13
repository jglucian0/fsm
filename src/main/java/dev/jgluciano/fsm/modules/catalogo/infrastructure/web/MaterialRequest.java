package dev.jgluciano.fsm.modules.catalogo.infrastructure.web;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record MaterialRequest(
        @NotBlank String nome,
        @NotBlank String unidade,
        @NotNull @Digits(integer = 12, fraction = 2) BigDecimal custoUnitario) {
}
