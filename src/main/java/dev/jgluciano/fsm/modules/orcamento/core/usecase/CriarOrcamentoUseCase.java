package dev.jgluciano.fsm.modules.orcamento.core.usecase;

import dev.jgluciano.fsm.modules.orcamento.core.model.ClienteDoOrcamentoInexistenteException;
import dev.jgluciano.fsm.modules.orcamento.core.model.Orcamento;
import dev.jgluciano.fsm.modules.orcamento.core.ports.ClienteConsulta;
import dev.jgluciano.fsm.modules.orcamento.core.ports.OrcamentoRepository;

import java.util.Objects;
import java.util.UUID;

public class CriarOrcamentoUseCase {

    private final OrcamentoRepository orcamentoRepository;
    private final ClienteConsulta clienteConsulta;

    public CriarOrcamentoUseCase(OrcamentoRepository orcamentoRepository, ClienteConsulta clienteConsulta) {
        this.orcamentoRepository = Objects.requireNonNull(orcamentoRepository, "orcamentoRepository");
        this.clienteConsulta = Objects.requireNonNull(clienteConsulta, "clienteConsulta");
    }

    public Orcamento executar(Comando comando) {
        Objects.requireNonNull(comando, "comando");

        if (!clienteConsulta.existe(comando.ownerId(), comando.clienteId())) {
            throw new ClienteDoOrcamentoInexistenteException(comando.clienteId());
        }
        return orcamentoRepository.salvar(Orcamento.novo(comando.ownerId(), comando.clienteId()));
    }

    public record Comando(UUID ownerId, UUID clienteId) {

        public Comando {
            Objects.requireNonNull(ownerId, "ownerId");
            Objects.requireNonNull(clienteId, "clienteId");
        }
    }
}
