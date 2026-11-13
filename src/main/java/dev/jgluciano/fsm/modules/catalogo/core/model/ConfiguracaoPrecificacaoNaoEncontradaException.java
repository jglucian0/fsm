package dev.jgluciano.fsm.modules.catalogo.core.model;

import java.util.UUID;

/** O dono informado ainda não cadastrou sua configuração de precificação. */
public class ConfiguracaoPrecificacaoNaoEncontradaException extends RuntimeException {

    public ConfiguracaoPrecificacaoNaoEncontradaException(UUID ownerId) {
        super("Configuração de precificação não encontrada para o dono " + ownerId);
    }
}
