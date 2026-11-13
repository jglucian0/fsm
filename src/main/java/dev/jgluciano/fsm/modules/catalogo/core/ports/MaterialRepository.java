package dev.jgluciano.fsm.modules.catalogo.core.ports;

import dev.jgluciano.fsm.modules.catalogo.core.model.Material;

import java.util.List;
import java.util.UUID;

public interface MaterialRepository {

    Material salvar(Material material);

    /** Compara o nome sem diferenciar maiúsculas de minúsculas. */
    boolean existePorOwnerENome(UUID ownerId, String nome);

    List<Material> listarPorOwner(UUID ownerId);
}
