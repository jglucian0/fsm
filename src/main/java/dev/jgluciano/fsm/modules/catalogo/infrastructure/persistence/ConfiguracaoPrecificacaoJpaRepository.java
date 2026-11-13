package dev.jgluciano.fsm.modules.catalogo.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ConfiguracaoPrecificacaoJpaRepository
        extends JpaRepository<ConfiguracaoPrecificacaoEntity, UUID> {
}
