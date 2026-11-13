package dev.jgluciano.fsm.modules.catalogo.infrastructure.persistence;

import dev.jgluciano.fsm.modules.catalogo.core.model.Servico;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ServicoPersistenceMapper {

    ServicoEntity toEntity(Servico servico);

    Servico toDomain(ServicoEntity entity);
}
