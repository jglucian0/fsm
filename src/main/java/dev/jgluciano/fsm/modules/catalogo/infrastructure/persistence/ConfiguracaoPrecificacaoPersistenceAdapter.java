package dev.jgluciano.fsm.modules.catalogo.infrastructure.persistence;

import dev.jgluciano.fsm.modules.catalogo.core.model.ConfiguracaoPrecificacao;
import dev.jgluciano.fsm.modules.catalogo.core.ports.ConfiguracaoPrecificacaoRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
class ConfiguracaoPrecificacaoPersistenceAdapter implements ConfiguracaoPrecificacaoRepository {

    private final ConfiguracaoPrecificacaoJpaRepository jpaRepository;
    private final ConfiguracaoPrecificacaoPersistenceMapper mapper;

    ConfiguracaoPrecificacaoPersistenceAdapter(ConfiguracaoPrecificacaoJpaRepository jpaRepository,
                                               ConfiguracaoPrecificacaoPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<ConfiguracaoPrecificacao> buscarPorOwner(UUID ownerId) {
        return jpaRepository.findById(ownerId).map(mapper::toDomain);
    }

    @Override
    public ConfiguracaoPrecificacao salvar(ConfiguracaoPrecificacao configuracao) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(configuracao)));
    }
}
