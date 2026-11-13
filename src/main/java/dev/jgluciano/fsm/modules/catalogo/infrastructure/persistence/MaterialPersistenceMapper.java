package dev.jgluciano.fsm.modules.catalogo.infrastructure.persistence;

import dev.jgluciano.fsm.modules.catalogo.core.model.Material;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MaterialPersistenceMapper {

    MaterialEntity toEntity(Material material);

    Material toDomain(MaterialEntity entity);
}
