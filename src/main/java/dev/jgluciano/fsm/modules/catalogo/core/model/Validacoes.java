package dev.jgluciano.fsm.modules.catalogo.core.model;

import java.math.BigDecimal;

/** Validações de entrada compartilhadas pelo domínio de precificação. */
public final class Validacoes {

    private Validacoes() {
    }

    public static void exigirNaoNegativo(BigDecimal valor, String campo) {
        if (valor.signum() < 0) {
            throw new PrecificacaoInvalidaException(campo + " não pode ser negativo");
        }
    }
}
