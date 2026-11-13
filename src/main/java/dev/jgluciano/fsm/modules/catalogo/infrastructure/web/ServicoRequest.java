package dev.jgluciano.fsm.modules.catalogo.infrastructure.web;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ServicoRequest(
        @NotBlank String nome,
        @NotNull @Digits(integer = 6, fraction = 2) BigDecimal horasPadrao) {
}
