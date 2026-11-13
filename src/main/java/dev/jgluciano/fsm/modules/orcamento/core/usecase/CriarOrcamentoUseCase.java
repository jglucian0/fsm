package dev.jgluciano.fsm.modules.orcamento.core.usecase;

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
        throw new UnsupportedOperationException("Red: ainda não implementado");
    }

    public record Comando(UUID ownerId, UUID clienteId) {

        public Comando {
            Objects.requireNonNull(ownerId, "ownerId");
            Objects.requireNonNull(clienteId, "clienteId");
        }
    }
}
