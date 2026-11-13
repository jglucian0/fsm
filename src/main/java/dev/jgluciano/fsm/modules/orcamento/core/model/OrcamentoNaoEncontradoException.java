package dev.jgluciano.fsm.modules.orcamento.core.model;

import java.util.UUID;

/** Orçamento inexistente ou pertencente a outro dono (não revelamos a diferença). */
public class OrcamentoNaoEncontradoException extends RuntimeException {

    public OrcamentoNaoEncontradoException(UUID id) {
        super("Orçamento não encontrado: " + id);
    }
}
