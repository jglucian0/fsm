package dev.jgluciano.fsm.modules.orcamento.core.usecase;

import dev.jgluciano.fsm.modules.orcamento.core.model.Orcamento;
import dev.jgluciano.fsm.modules.orcamento.core.model.StatusOrcamento;
import dev.jgluciano.fsm.modules.orcamento.core.ports.OrcamentoRepository;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ListarOrcamentosUseCase {

    private final OrcamentoRepository orcamentoRepository;

    public ListarOrcamentosUseCase(OrcamentoRepository orcamentoRepository) {
        this.orcamentoRepository = Objects.requireNonNull(orcamentoRepository, "orcamentoRepository");
    }

    public List<Orcamento> executar(Comando comando) {
        Objects.requireNonNull(comando, "comando");

        if (comando.status() == null) {
            return orcamentoRepository.listarPorOwner(comando.ownerId());
        }
        return orcamentoRepository.listarPorOwnerEStatus(comando.ownerId(), comando.status());
    }

    /** {@code status} nulo significa "todos os status". */
    public record Comando(UUID ownerId, StatusOrcamento status) {

        public Comando {
            Objects.requireNonNull(ownerId, "ownerId");
        }
    }
}
