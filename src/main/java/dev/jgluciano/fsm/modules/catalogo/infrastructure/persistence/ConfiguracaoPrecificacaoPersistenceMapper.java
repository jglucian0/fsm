package dev.jgluciano.fsm.modules.catalogo.infrastructure.persistence;

import dev.jgluciano.fsm.modules.catalogo.core.model.ConfiguracaoPrecificacao;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConfiguracaoPrecificacaoPersistenceMapper {

    ConfiguracaoPrecificacaoEntity toEntity(ConfiguracaoPrecificacao configuracao);

    ConfiguracaoPrecificacao toDomain(ConfiguracaoPrecificacaoEntity entity);
}
