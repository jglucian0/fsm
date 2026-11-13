package dev.jgluciano.fsm.modules.catalogo.infrastructure.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Dono dos dados nas requisições. Usuário único até a fase de autenticação (ADR 0008):
 * quando entrar o Spring Security, só esta classe muda.
 */
@Component
class DonoAtual {

    private final UUID id;

    DonoAtual(@Value("${fsm.owner-id:00000000-0000-0000-0000-000000000001}") UUID id) {
        this.id = id;
    }

    UUID id() {
        return id;
    }
}
