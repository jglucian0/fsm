package dev.jgluciano.fsm.modules.catalogo.core.usecase;

import dev.jgluciano.fsm.modules.catalogo.core.model.Material;
import dev.jgluciano.fsm.modules.catalogo.core.ports.MaterialRepository;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ListarMateriaisUseCase {

    private final MaterialRepository materialRepository;

    public ListarMateriaisUseCase(MaterialRepository materialRepository) {
        this.materialRepository = Objects.requireNonNull(materialRepository, "materialRepository");
    }

    public List<Material> executar(UUID ownerId) {
        Objects.requireNonNull(ownerId, "ownerId");
        return materialRepository.listarPorOwner(ownerId);
    }
}
