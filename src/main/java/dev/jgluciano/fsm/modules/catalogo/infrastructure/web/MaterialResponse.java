package dev.jgluciano.fsm.modules.catalogo.infrastructure.web;

import dev.jgluciano.fsm.modules.catalogo.core.model.Material;

import java.math.BigDecimal;
import java.util.UUID;

public record MaterialResponse(UUID id, String nome, String unidade, BigDecimal custoUnitario) {

    static MaterialResponse de(Material material) {
        return new MaterialResponse(material.id(), material.nome(), material.unidade(), material.custoUnitario());
    }
}
