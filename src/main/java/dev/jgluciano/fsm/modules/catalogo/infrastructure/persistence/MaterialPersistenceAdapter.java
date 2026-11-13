package dev.jgluciano.fsm.modules.catalogo.infrastructure.persistence;

import dev.jgluciano.fsm.modules.catalogo.core.model.Material;
import dev.jgluciano.fsm.modules.catalogo.core.ports.MaterialRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
class MaterialPersistenceAdapter implements MaterialRepository {

    private final MaterialJpaRepository jpaRepository;
    private final MaterialPersistenceMapper mapper;

    MaterialPersistenceAdapter(MaterialJpaRepository jpaRepository, MaterialPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Material salvar(Material material) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(material)));
    }

    @Override
    public boolean existePorOwnerENome(UUID ownerId, String nome) {
        return jpaRepository.existsByOwnerIdAndNomeIgnoreCase(ownerId, nome);
    }

    @Override
    public List<Material> listarPorOwner(UUID ownerId) {
        return jpaRepository.findByOwnerIdOrderByNomeAsc(ownerId).stream().map(mapper::toDomain).toList();
    }
}
