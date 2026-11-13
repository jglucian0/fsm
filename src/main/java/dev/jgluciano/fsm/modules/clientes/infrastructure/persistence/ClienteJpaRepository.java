package dev.jgluciano.fsm.modules.clientes.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ClienteJpaRepository extends JpaRepository<ClienteEntity, UUID> {

    List<ClienteEntity> findByOwnerIdOrderByNomeAsc(UUID ownerId);
}
