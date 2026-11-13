package dev.jgluciano.fsm.modules.clientes.core.model;

import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Cliente do eletricista (ADR 0009). Telefone, e-mail e endereço são opcionais: em branco vale como ausente (null).
 * O telefone é guardado só com dígitos (DDD + número), sem o prefixo 55.
 */
public record Cliente(UUID id, UUID ownerId, String nome, String telefone, String email, String endereco) {

    public static final int TAMANHO_MAXIMO_NOME = 120;
    public static final int TAMANHO_MAXIMO_ENDERECO = 255;

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final String PREFIXO_PAIS = "55";

    public Cliente {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(ownerId, "ownerId");
        Objects.requireNonNull(nome, "nome");

        nome = nome.strip();
        if (nome.isEmpty()) {
            throw new ClienteInvalidoException("Nome é obrigatório");
        }
        if (nome.length() > TAMANHO_MAXIMO_NOME) {
            throw new ClienteInvalidoException("Nome deve ter no máximo " + TAMANHO_MAXIMO_NOME + " caracteres");
        }
        telefone = normalizarTelefone(telefone);
        email = normalizarEmail(email);
        endereco = normalizarEndereco(endereco);
    }

    public static Cliente novo(UUID ownerId, String nome, String telefone, String email, String endereco) {
        return new Cliente(UUID.randomUUID(), ownerId, nome, telefone, email, endereco);
    }

    private static String normalizarTelefone(String telefone) {
        if (telefone == null || telefone.isBlank()) {
            return null;
        }
        var digitos = telefone.replaceAll("\\D", "");
        if ((digitos.length() == 12 || digitos.length() == 13) && digitos.startsWith(PREFIXO_PAIS)) {
            digitos = digitos.substring(PREFIXO_PAIS.length());
        }
        if (digitos.length() != 10 && digitos.length() != 11) {
            throw new ClienteInvalidoException("Telefone inválido: informe DDD e número");
        }
        return digitos;
    }

    private static String normalizarEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        var normalizado = email.strip().toLowerCase(Locale.ROOT);
        if (!EMAIL.matcher(normalizado).matches()) {
            throw new ClienteInvalidoException("E-mail inválido");
        }
        return normalizado;
    }

    private static String normalizarEndereco(String endereco) {
        if (endereco == null || endereco.isBlank()) {
            return null;
        }
        var normalizado = endereco.strip();
        if (normalizado.length() > TAMANHO_MAXIMO_ENDERECO) {
            throw new ClienteInvalidoException(
                    "Endereço deve ter no máximo " + TAMANHO_MAXIMO_ENDERECO + " caracteres");
        }
        return normalizado;
    }
}
