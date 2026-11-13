package dev.jgluciano.fsm.modules.clientes.core.ports;

import dev.jgluciano.fsm.modules.clientes.core.model.Cliente;

import java.util.List;
import java.util.UUID;

public interface ClienteRepository {

    Cliente salvar(Cliente cliente);

    List<Cliente> listarPorOwner(UUID ownerId);
}
