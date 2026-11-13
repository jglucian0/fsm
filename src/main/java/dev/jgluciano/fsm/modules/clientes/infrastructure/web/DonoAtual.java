package dev.jgluciano.fsm.modules.clientes.infrastructure.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Dono dos dados nas requisições. Cópia própria do módulo (ADR 0010): clientes não importa de catalogo.
 * Quando entrar autenticação, um mecanismo único substitui as cópias.
 */
@Component("clientesDonoAtual")
class DonoAtual {

    private final UUID id;

    DonoAtual(@Value("${fsm.owner-id:00000000-0000-0000-0000-000000000001}") UUID id) {
        this.id = id;
    }

    UUID id() {
        return id;
    }
}
