package dev.jgluciano.fsm.modules.clientes.infrastructure.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Aqui só o formato (400). Telefone, e-mail e endereço são validados pelo domínio (422). */
public record ClienteRequest(
        @NotBlank String nome,
        String telefone,
        @Size(max = 254) String email,
        String endereco) {
}
