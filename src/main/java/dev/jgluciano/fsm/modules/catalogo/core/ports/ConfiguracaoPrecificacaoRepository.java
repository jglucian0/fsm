package dev.jgluciano.fsm.modules.catalogo.core.ports;

import dev.jgluciano.fsm.modules.catalogo.core.model.ConfiguracaoPrecificacao;

import java.util.Optional;
import java.util.UUID;

public interface ConfiguracaoPrecificacaoRepository {

    Optional<ConfiguracaoPrecificacao> buscarPorOwner(UUID ownerId);
}
