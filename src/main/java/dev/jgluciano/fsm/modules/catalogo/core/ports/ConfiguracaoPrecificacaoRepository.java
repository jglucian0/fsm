package dev.jgluciano.fsm.modules.catalogo.core.ports;

import dev.jgluciano.fsm.modules.catalogo.core.model.ConfiguracaoPrecificacao;

import java.util.Optional;
import java.util.UUID;

public interface ConfiguracaoPrecificacaoRepository {

    Optional<ConfiguracaoPrecificacao> buscarPorOwner(UUID ownerId);

    /** Cria ou substitui a configuração do dono (uma por dono). */
    ConfiguracaoPrecificacao salvar(ConfiguracaoPrecificacao configuracao);
}
