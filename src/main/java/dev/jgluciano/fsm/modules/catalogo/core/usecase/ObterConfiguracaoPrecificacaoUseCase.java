package dev.jgluciano.fsm.modules.catalogo.core.usecase;

import dev.jgluciano.fsm.modules.catalogo.core.model.ConfiguracaoPrecificacao;
import dev.jgluciano.fsm.modules.catalogo.core.model.ConfiguracaoPrecificacaoNaoEncontradaException;
import dev.jgluciano.fsm.modules.catalogo.core.ports.ConfiguracaoPrecificacaoRepository;

import java.util.Objects;
import java.util.UUID;

public class ObterConfiguracaoPrecificacaoUseCase {

    private final ConfiguracaoPrecificacaoRepository repository;

    public ObterConfiguracaoPrecificacaoUseCase(ConfiguracaoPrecificacaoRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public ConfiguracaoPrecificacao executar(UUID ownerId) {
        Objects.requireNonNull(ownerId, "ownerId");
        return repository.buscarPorOwner(ownerId)
                .orElseThrow(() -> new ConfiguracaoPrecificacaoNaoEncontradaException(ownerId));
    }
}
