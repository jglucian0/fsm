package dev.jgluciano.fsm.modules.catalogo.core.usecase;

import dev.jgluciano.fsm.modules.catalogo.core.model.Material;
import dev.jgluciano.fsm.modules.catalogo.core.model.MaterialJaCadastradoException;
import dev.jgluciano.fsm.modules.catalogo.core.ports.MaterialRepository;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class CadastrarMaterialUseCase {

    private final MaterialRepository materialRepository;

    public CadastrarMaterialUseCase(MaterialRepository materialRepository) {
        this.materialRepository = Objects.requireNonNull(materialRepository, "materialRepository");
    }

    public Material executar(Comando comando) {
        Objects.requireNonNull(comando, "comando");

        var material = Material.novo(comando.ownerId(), comando.nome(), comando.unidade(),
                comando.custoUnitario());

        if (materialRepository.existePorOwnerENome(material.ownerId(), material.nome())) {
            throw new MaterialJaCadastradoException(material.nome());
        }
        return materialRepository.salvar(material);
    }

    public record Comando(UUID ownerId, String nome, String unidade, BigDecimal custoUnitario) {

        public Comando {
            Objects.requireNonNull(ownerId, "ownerId");
            Objects.requireNonNull(nome, "nome");
            Objects.requireNonNull(unidade, "unidade");
            Objects.requireNonNull(custoUnitario, "custoUnitario");
        }
    }
}
