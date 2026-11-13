package dev.jgluciano.fsm.modules.catalogo.core.usecase;

import dev.jgluciano.fsm.modules.catalogo.core.model.Servico;
import dev.jgluciano.fsm.modules.catalogo.core.ports.ServicoRepository;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ListarServicosUseCase {

    private final ServicoRepository servicoRepository;

    public ListarServicosUseCase(ServicoRepository servicoRepository) {
        this.servicoRepository = Objects.requireNonNull(servicoRepository, "servicoRepository");
    }

    public List<Servico> executar(UUID ownerId) {
        Objects.requireNonNull(ownerId, "ownerId");
        return servicoRepository.listarPorOwner(ownerId);
    }
}
