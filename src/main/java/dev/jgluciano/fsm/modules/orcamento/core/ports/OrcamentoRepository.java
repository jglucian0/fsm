package dev.jgluciano.fsm.modules.orcamento.core.ports;

import dev.jgluciano.fsm.modules.orcamento.core.model.Orcamento;
import dev.jgluciano.fsm.modules.orcamento.core.model.StatusOrcamento;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrcamentoRepository {

    Orcamento salvar(Orcamento orcamento);

    /** Só devolve o orçamento se pertencer ao dono informado. */
    Optional<Orcamento> buscarPorId(UUID ownerId, UUID id);

    List<Orcamento> listarPorOwner(UUID ownerId);

    List<Orcamento> listarPorOwnerEStatus(UUID ownerId, StatusOrcamento status);
}
