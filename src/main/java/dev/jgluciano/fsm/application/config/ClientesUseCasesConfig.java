package dev.jgluciano.fsm.application.config;

import dev.jgluciano.fsm.modules.clientes.core.ports.ClienteRepository;
import dev.jgluciano.fsm.modules.clientes.core.usecase.CadastrarClienteUseCase;
import dev.jgluciano.fsm.modules.clientes.core.usecase.ListarClientesUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** O core é Java puro (sem @Service): aqui os casos de uso viram beans do Spring. */
@Configuration
public class ClientesUseCasesConfig {

    @Bean
    CadastrarClienteUseCase cadastrarClienteUseCase(ClienteRepository repository) {
        return new CadastrarClienteUseCase(repository);
    }

    @Bean
    ListarClientesUseCase listarClientesUseCase(ClienteRepository repository) {
        return new ListarClientesUseCase(repository);
    }
}
