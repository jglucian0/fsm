package dev.jgluciano.fsm.modules.orcamento.core.ports;

import java.util.UUID;

/**
 * Porta para confirmar que o cliente existe, sem o orçamento conhecer o módulo clientes (regra 4).
 * O adaptador em infrastructure usa a api pública de clientes.
 */
public interface ClienteConsulta {

    boolean existe(UUID ownerId, UUID clienteId);
}
