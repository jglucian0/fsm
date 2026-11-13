package dev.jgluciano.fsm.modules.catalogo.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ServicoJpaRepository extends JpaRepository<ServicoEntity, UUID> {

    boolean existsByOwnerIdAndNomeIgnoreCase(UUID ownerId, String nome);

    List<ServicoEntity> findByOwnerIdOrderByNomeAsc(UUID ownerId);
}
