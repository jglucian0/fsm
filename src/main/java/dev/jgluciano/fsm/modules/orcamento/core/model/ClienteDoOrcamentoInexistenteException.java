package dev.jgluciano.fsm.modules.orcamento.core.model;

import java.util.UUID;

/** O cliente informado ao criar o orçamento não existe para o dono atual. */
public class ClienteDoOrcamentoInexistenteException extends RuntimeException {

    public ClienteDoOrcamentoInexistenteException(UUID clienteId) {
        super("Cliente não encontrado para o orçamento: " + clienteId);
    }
}
