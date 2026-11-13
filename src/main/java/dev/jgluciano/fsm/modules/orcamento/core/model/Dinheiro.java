package dev.jgluciano.fsm.modules.orcamento.core.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/** Regras comuns de valores do orçamento: escala 2, HALF_UP, sem negativos. */
final class Dinheiro {

    private Dinheiro() {
    }

    static BigDecimal normalizar(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP);
    }

    /** Rejeita negativo e devolve o valor normalizado. A checagem é no valor informado, antes de arredondar. */
    static BigDecimal naoNegativo(BigDecimal valor, String nome) {
        Objects.requireNonNull(valor, nome);
        if (valor.signum() < 0) {
            throw new OrcamentoInvalidoException(nome + " não pode ser negativo");
        }
        return normalizar(valor);
    }
}
