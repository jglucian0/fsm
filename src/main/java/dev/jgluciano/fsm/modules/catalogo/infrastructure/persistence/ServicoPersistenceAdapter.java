package dev.jgluciano.fsm.modules.catalogo.infrastructure.persistence;

import dev.jgluciano.fsm.modules.catalogo.core.model.Servico;
import dev.jgluciano.fsm.modules.catalogo.core.ports.ServicoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
class ServicoPersistenceAdapter implements ServicoRepository {

    private final ServicoJpaRepository jpaRepository;
    private final ServicoPersistenceMapper mapper;

    ServicoPersistenceAdapter(ServicoJpaRepository jpaRepository, ServicoPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Servico salvar(Servico servico) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(servico)));
    }

    @Override
    public boolean existePorOwnerENome(UUID ownerId, String nome) {
        return jpaRepository.existsByOwnerIdAndNomeIgnoreCase(ownerId, nome);
    }

    @Override
    public List<Servico> listarPorOwner(UUID ownerId) {
        return jpaRepository.findByOwnerIdOrderByNomeAsc(ownerId).stream().map(mapper::toDomain).toList();
    }
}
