package dev.jgluciano.fsm.modules.orcamento.core.usecase;

import dev.jgluciano.fsm.modules.orcamento.core.ports.ClienteConsulta;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/** Fake de teste: só conhece os clientes cadastrados explicitamente, por dono. */
class ClienteConsultaFake implements ClienteConsulta {

    private record Chave(UUID ownerId, UUID clienteId) {
    }

    private final Set<Chave> clientes = new HashSet<>();

    void cadastrar(UUID ownerId, UUID clienteId) {
        clientes.add(new Chave(ownerId, clienteId));
    }

    @Override
    public boolean existe(UUID ownerId, UUID clienteId) {
        return clientes.contains(new Chave(ownerId, clienteId));
    }
}
