package dev.jgluciano.fsm.modules.clientes.core.usecase;

import dev.jgluciano.fsm.modules.clientes.core.model.Cliente;
import dev.jgluciano.fsm.modules.clientes.core.ports.ClienteRepository;

import java.util.Objects;
import java.util.UUID;

/** Nomes repetidos são permitidos (duas pessoas podem se chamar igual), então não há checagem de duplicidade. */
public class CadastrarClienteUseCase {

    private final ClienteRepository clienteRepository;

    public CadastrarClienteUseCase(ClienteRepository clienteRepository) {
        this.clienteRepository = Objects.requireNonNull(clienteRepository, "clienteRepository");
    }

    public Cliente executar(Comando comando) {
        Objects.requireNonNull(comando, "comando");

        var cliente = Cliente.novo(comando.ownerId(), comando.nome(), comando.telefone(), comando.email(),
                comando.endereco());
        return clienteRepository.salvar(cliente);
    }

    public record Comando(UUID ownerId, String nome, String telefone, String email, String endereco) {

        public Comando {
            Objects.requireNonNull(ownerId, "ownerId");
            Objects.requireNonNull(nome, "nome");
        }
    }
}
