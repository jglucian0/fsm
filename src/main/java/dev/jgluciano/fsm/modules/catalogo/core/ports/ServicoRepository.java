package dev.jgluciano.fsm.modules.catalogo.core.ports;

import dev.jgluciano.fsm.modules.catalogo.core.model.Servico;

import java.util.List;
import java.util.UUID;

public interface ServicoRepository {

    Servico salvar(Servico servico);

    /** Compara o nome sem diferenciar maiúsculas de minúsculas. */
    boolean existePorOwnerENome(UUID ownerId, String nome);

    List<Servico> listarPorOwner(UUID ownerId);
}
