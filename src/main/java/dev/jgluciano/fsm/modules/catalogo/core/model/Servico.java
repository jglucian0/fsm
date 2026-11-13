package dev.jgluciano.fsm.modules.catalogo.core.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.UUID;

/** Serviço do catálogo, com horas padrão em escala 2 (ADR 0007). */
public record Servico(UUID id, UUID ownerId, String nome, BigDecimal horasPadrao) {

    public static final int TAMANHO_MAXIMO_NOME = 120;
    private static final int ESCALA_HORAS = 2;

    public Servico {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(ownerId, "ownerId");
        Objects.requireNonNull(nome, "nome");
        Objects.requireNonNull(horasPadrao, "horasPadrao");

        nome = nome.strip();

        if (nome.isEmpty()) {
            throw new CatalogoInvalidoException("Nome é obrigatório");
        }
        if (nome.length() > TAMANHO_MAXIMO_NOME) {
            throw new CatalogoInvalidoException("Nome deve ter no máximo " + TAMANHO_MAXIMO_NOME + " caracteres");
        }
        if (horasPadrao.signum() <= 0) {
            throw new CatalogoInvalidoException("Horas padrão devem ser maiores que zero");
        }
        horasPadrao = horasPadrao.setScale(ESCALA_HORAS, RoundingMode.HALF_UP);
    }

    public static Servico novo(UUID ownerId, String nome, BigDecimal horasPadrao) {
        return new Servico(UUID.randomUUID(), ownerId, nome, horasPadrao);
    }
}
