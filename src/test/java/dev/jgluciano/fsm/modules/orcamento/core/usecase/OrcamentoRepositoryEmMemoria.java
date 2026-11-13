package dev.jgluciano.fsm.modules.orcamento.core.usecase;

import dev.jgluciano.fsm.modules.orcamento.core.model.Orcamento;
import dev.jgluciano.fsm.modules.orcamento.core.model.StatusOrcamento;
import dev.jgluciano.fsm.modules.orcamento.core.ports.OrcamentoRepository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/** Fake de teste: respeita o isolamento por dono como o adaptador real deve respeitar. */
class OrcamentoRepositoryEmMemoria implements OrcamentoRepository {

    private final Map<UUID, Orcamento> armazenados = new LinkedHashMap<>();
    int totalDeSalvamentos;

    @Override
    public Orcamento salvar(Orcamento orcamento) {
        totalDeSalvamentos++;
        armazenados.put(orcamento.id(), orcamento);
        return orcamento;
    }

    @Override
    public Optional<Orcamento> buscarPorId(UUID ownerId, UUID id) {
        return Optional.ofNullable(armazenados.get(id)).filter(o -> o.ownerId().equals(ownerId));
    }

    @Override
    public List<Orcamento> listarPorOwner(UUID ownerId) {
        return armazenados.values().stream().filter(o -> o.ownerId().equals(ownerId)).toList();
    }

    @Override
    public List<Orcamento> listarPorOwnerEStatus(UUID ownerId, StatusOrcamento status) {
        return listarPorOwner(ownerId).stream().filter(o -> o.status() == status).toList();
    }
}
