package dev.jgluciano.fsm.modules.clientes.infrastructure.persistence;

import dev.jgluciano.fsm.modules.clientes.core.model.Cliente;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClientePersistenceMapper {

    ClienteEntity toEntity(Cliente cliente);

    Cliente toDomain(ClienteEntity entity);
}
