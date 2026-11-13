package dev.jgluciano.fsm.modules.catalogo.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MaterialJpaRepository extends JpaRepository<MaterialEntity, UUID> {

    boolean existsByOwnerIdAndNomeIgnoreCase(UUID ownerId, String nome);

    List<MaterialEntity> findByOwnerIdOrderByNomeAsc(UUID ownerId);
}
