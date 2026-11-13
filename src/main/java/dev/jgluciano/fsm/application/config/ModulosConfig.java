package dev.jgluciano.fsm.application.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * A aplicação principal fica em {@code application}, então os módulos precisam ser varridos explicitamente:
 * componentes (adaptadores, controllers, mappers), entidades JPA e repositórios Spring Data.
 */
@Configuration
@ComponentScan("dev.jgluciano.fsm.modules")
@EntityScan("dev.jgluciano.fsm.modules")
@EnableJpaRepositories("dev.jgluciano.fsm.modules")
public class ModulosConfig {
}
