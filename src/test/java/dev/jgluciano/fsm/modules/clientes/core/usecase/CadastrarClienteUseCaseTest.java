package dev.jgluciano.fsm.modules.clientes.core.usecase;

import dev.jgluciano.fsm.modules.clientes.core.model.Cliente;
import dev.jgluciano.fsm.modules.clientes.core.model.ClienteInvalidoException;
import dev.jgluciano.fsm.modules.clientes.core.ports.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Fase 2a, RED. Ainda não existem: CadastrarClienteUseCase (com Comando) e ClienteRepository (porta).
 * Nomes repetidos são permitidos (dois "João Silva" são pessoas diferentes), então não há checagem de duplicidade.
 */
@ExtendWith(MockitoExtension.class)
class CadastrarClienteUseCaseTest {

    private final UUID ownerId = UUID.randomUUID();

    @Mock
    private ClienteRepository clienteRepository;

    private CadastrarClienteUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CadastrarClienteUseCase(clienteRepository);
    }

    @Test
    @DisplayName("cadastra e salva o cliente do dono informado, com dados normalizados")
    void cadastraEPersisteCliente() {
        when(clienteRepository.salvar(any(Cliente.class))).thenAnswer(i -> i.getArgument(0));

        var cliente = useCase.executar(new CadastrarClienteUseCase.Comando(
                ownerId, "  João da Silva ", "(11) 98765-4321", "Joao@Exemplo.com", "Rua das Flores, 100"));

        var captor = ArgumentCaptor.forClass(Cliente.class);
        verify(clienteRepository).salvar(captor.capture());
        assertThat(captor.getValue()).isEqualTo(cliente);
        assertThat(cliente.ownerId()).isEqualTo(ownerId);
        assertThat(cliente.nome()).isEqualTo("João da Silva");
        assertThat(cliente.telefone()).isEqualTo("11987654321");
        assertThat(cliente.email()).isEqualTo("joao@exemplo.com");
    }

    @Test
    @DisplayName("aceita cliente só com nome")
    void aceitaApenasNome() {
        when(clienteRepository.salvar(any(Cliente.class))).thenAnswer(i -> i.getArgument(0));

        var cliente = useCase.executar(new CadastrarClienteUseCase.Comando(ownerId, "Maria", null, null, null));

        assertThat(cliente.telefone()).isNull();
        assertThat(cliente.email()).isNull();
        assertThat(cliente.endereco()).isNull();
    }

    @Test
    @DisplayName("não salva quando os dados são inválidos")
    void naoSalvaClienteInvalido() {
        assertThatThrownBy(() -> useCase.executar(
                new CadastrarClienteUseCase.Comando(ownerId, "Maria", "123", null, null)))
                .isInstanceOf(ClienteInvalidoException.class);

        verify(clienteRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("rejeita comando sem dono ou sem nome")
    void rejeitaComandoComNulos() {
        assertThatNullPointerException().isThrownBy(() ->
                new CadastrarClienteUseCase.Comando(null, "Maria", null, null, null));
        assertThatNullPointerException().isThrownBy(() ->
                new CadastrarClienteUseCase.Comando(ownerId, null, null, null, null));
    }
}
