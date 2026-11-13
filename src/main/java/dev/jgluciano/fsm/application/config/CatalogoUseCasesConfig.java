package dev.jgluciano.fsm.application.config;

import dev.jgluciano.fsm.modules.catalogo.core.ports.ConfiguracaoPrecificacaoRepository;
import dev.jgluciano.fsm.modules.catalogo.core.ports.MaterialRepository;
import dev.jgluciano.fsm.modules.catalogo.core.ports.ServicoRepository;
import dev.jgluciano.fsm.modules.catalogo.core.usecase.CadastrarMaterialUseCase;
import dev.jgluciano.fsm.modules.catalogo.core.usecase.CadastrarServicoUseCase;
import dev.jgluciano.fsm.modules.catalogo.core.usecase.CalcularPrecoServicoUseCase;
import dev.jgluciano.fsm.modules.catalogo.core.usecase.DefinirConfiguracaoPrecificacaoUseCase;
import dev.jgluciano.fsm.modules.catalogo.core.usecase.ListarMateriaisUseCase;
import dev.jgluciano.fsm.modules.catalogo.core.usecase.ListarServicosUseCase;
import dev.jgluciano.fsm.modules.catalogo.core.usecase.ObterConfiguracaoPrecificacaoUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** O core é Java puro (sem @Service): aqui os casos de uso viram beans do Spring. */
@Configuration
public class CatalogoUseCasesConfig {

    @Bean
    CadastrarMaterialUseCase cadastrarMaterialUseCase(MaterialRepository repository) {
        return new CadastrarMaterialUseCase(repository);
    }

    @Bean
    ListarMateriaisUseCase listarMateriaisUseCase(MaterialRepository repository) {
        return new ListarMateriaisUseCase(repository);
    }

    @Bean
    CadastrarServicoUseCase cadastrarServicoUseCase(ServicoRepository repository) {
        return new CadastrarServicoUseCase(repository);
    }

    @Bean
    ListarServicosUseCase listarServicosUseCase(ServicoRepository repository) {
        return new ListarServicosUseCase(repository);
    }

    @Bean
    DefinirConfiguracaoPrecificacaoUseCase definirConfiguracaoPrecificacaoUseCase(
            ConfiguracaoPrecificacaoRepository repository) {
        return new DefinirConfiguracaoPrecificacaoUseCase(repository);
    }

    @Bean
    ObterConfiguracaoPrecificacaoUseCase obterConfiguracaoPrecificacaoUseCase(
            ConfiguracaoPrecificacaoRepository repository) {
        return new ObterConfiguracaoPrecificacaoUseCase(repository);
    }

    @Bean
    CalcularPrecoServicoUseCase calcularPrecoServicoUseCase(ConfiguracaoPrecificacaoRepository repository) {
        return new CalcularPrecoServicoUseCase(repository);
    }
}
