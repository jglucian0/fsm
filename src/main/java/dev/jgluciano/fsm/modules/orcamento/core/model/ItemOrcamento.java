package dev.jgluciano.fsm.modules.orcamento.core.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Item do orçamento (ADR 0011). É um snapshot: o valor é copiado no momento, então mudanças no catálogo
 * não alteram orçamentos existentes.
 */
public record ItemOrcamento(String descricao, TipoItem tipo, BigDecimal quantidade, BigDecimal valorUnitario) {

    public static final int TAMANHO_MAXIMO_DESCRICAO = 200;

    public ItemOrcamento {
        Objects.requireNonNull(descricao, "descricao");
        Objects.requireNonNull(tipo, "tipo");
        Objects.requireNonNull(quantidade, "quantidade");

        descricao = descricao.strip();
        if (descricao.isEmpty()) {
            throw new OrcamentoInvalidoException("Descrição é obrigatória");
        }
        if (descricao.length() > TAMANHO_MAXIMO_DESCRICAO) {
            throw new OrcamentoInvalidoException(
                    "Descrição deve ter no máximo " + TAMANHO_MAXIMO_DESCRICAO + " caracteres");
        }
        quantidade = Dinheiro.normalizar(quantidade);
        if (quantidade.signum() <= 0) {
            throw new OrcamentoInvalidoException("Quantidade deve ser maior que zero");
        }
        valorUnitario = Dinheiro.naoNegativo(valorUnitario, "Valor unitário");
    }

    /** Quantidade x valor unitário, arredondado uma vez (HALF_UP, escala 2). */
    public BigDecimal subtotal() {
        return Dinheiro.normalizar(quantidade.multiply(valorUnitario));
    }
}
