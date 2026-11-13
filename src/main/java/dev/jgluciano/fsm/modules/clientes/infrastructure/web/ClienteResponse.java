package dev.jgluciano.fsm.modules.clientes.infrastructure.web;

import dev.jgluciano.fsm.modules.clientes.core.model.Cliente;

import java.util.UUID;

public record ClienteResponse(UUID id, String nome, String telefone, String email, String endereco) {

    static ClienteResponse de(Cliente cliente) {
        return new ClienteResponse(cliente.id(), cliente.nome(), cliente.telefone(), cliente.email(),
                cliente.endereco());
    }
}
