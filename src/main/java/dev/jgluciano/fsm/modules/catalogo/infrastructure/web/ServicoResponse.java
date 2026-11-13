package dev.jgluciano.fsm.modules.catalogo.infrastructure.web;

import dev.jgluciano.fsm.modules.catalogo.core.model.Servico;

import java.math.BigDecimal;
import java.util.UUID;

public record ServicoResponse(UUID id, String nome, BigDecimal horasPadrao) {

    static ServicoResponse de(Servico servico) {
        return new ServicoResponse(servico.id(), servico.nome(), servico.horasPadrao());
    }
}
