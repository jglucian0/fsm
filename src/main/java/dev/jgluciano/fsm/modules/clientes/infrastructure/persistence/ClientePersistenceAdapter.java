package dev.jgluciano.fsm.modules.clientes.infrastructure.persistence;

import dev.jgluciano.fsm.modules.clientes.core.model.Cliente;
import dev.jgluciano.fsm.modules.clientes.core.ports.ClienteRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
class ClientePersistenceAdapter implements ClienteRepository {

    private final ClienteJpaRepository jpaRepository;
    private final ClientePersistenceMapper mapper;

    ClientePersistenceAdapter(ClienteJpaRepository jpaRepository, ClientePersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Cliente salvar(Cliente cliente) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(cliente)));
    }

    @Override
    public List<Cliente> listarPorOwner(UUID ownerId) {
        return jpaRepository.findByOwnerIdOrderByNomeAsc(ownerId).stream().map(mapper::toDomain).toList();
    }
}
