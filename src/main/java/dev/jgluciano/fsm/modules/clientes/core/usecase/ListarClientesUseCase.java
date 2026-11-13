package dev.jgluciano.fsm.modules.clientes.core.usecase;

import dev.jgluciano.fsm.modules.clientes.core.model.Cliente;
import dev.jgluciano.fsm.modules.clientes.core.ports.ClienteRepository;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ListarClientesUseCase {

    private final ClienteRepository clienteRepository;

    public ListarClientesUseCase(ClienteRepository clienteRepository) {
        this.clienteRepository = Objects.requireNonNull(clienteRepository, "clienteRepository");
    }

    public List<Cliente> executar(UUID ownerId) {
        Objects.requireNonNull(ownerId, "ownerId");
        return clienteRepository.listarPorOwner(ownerId);
    }
}
