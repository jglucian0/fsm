package dev.jgluciano.fsm.modules.catalogo.core.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.UUID;

/** Material do catálogo, com custo unitário em escala 2 (ADR 0006). */
public record Material(UUID id, UUID ownerId, String nome, String unidade, BigDecimal custoUnitario) {

    public static final int TAMANHO_MAXIMO_NOME = 120;
    private static final int ESCALA_DINHEIRO = 2;

    public Material {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(ownerId, "ownerId");
        Objects.requireNonNull(nome, "nome");
        Objects.requireNonNull(unidade, "unidade");
        Objects.requireNonNull(custoUnitario, "custoUnitario");

        nome = nome.strip();
        unidade = unidade.strip();

        if (nome.isEmpty()) {
            throw new CatalogoInvalidoException("Nome é obrigatório");
        }
        if (nome.length() > TAMANHO_MAXIMO_NOME) {
            throw new CatalogoInvalidoException("Nome deve ter no máximo " + TAMANHO_MAXIMO_NOME + " caracteres");
        }
        if (unidade.isEmpty()) {
            throw new CatalogoInvalidoException("Unidade é obrigatória");
        }
        if (custoUnitario.signum() < 0) {
            throw new CatalogoInvalidoException("Custo unitário não pode ser negativo");
        }
        custoUnitario = custoUnitario.setScale(ESCALA_DINHEIRO, RoundingMode.HALF_UP);
    }

    public static Material novo(UUID ownerId, String nome, String unidade, BigDecimal custoUnitario) {
        return new Material(UUID.randomUUID(), ownerId, nome, unidade, custoUnitario);
    }
}
