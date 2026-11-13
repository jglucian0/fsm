package dev.jgluciano.fsm.modules.clientes.core.usecase;

import dev.jgluciano.fsm.modules.clientes.core.model.Cliente;
import dev.jgluciano.fsm.modules.clientes.core.ports.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/** Fase 2a, RED. Ainda não existe: ListarClientesUseCase. */
@ExtendWith(MockitoExtension.class)
class ListarClientesUseCaseTest {

    private final UUID ownerId = UUID.randomUUID();

    @Mock
    private ClienteRepository clienteRepository;

    private ListarClientesUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ListarClientesUseCase(clienteRepository);
    }

    @Test
    @DisplayName("lista os clientes do dono informado")
    void listaClientesDoOwner() {
        var clientes = List.of(
                Cliente.novo(ownerId, "Ana", null, null, null),
                Cliente.novo(ownerId, "Bruno", "11987654321", null, null));
        when(clienteRepository.listarPorOwner(ownerId)).thenReturn(clientes);

        assertThat(useCase.executar(ownerId)).containsExactlyElementsOf(clientes);
        verify(clienteRepository).listarPorOwner(ownerId);
        verifyNoMoreInteractions(clienteRepository);
    }

    @Test
    @DisplayName("devolve lista vazia quando não há clientes")
    void devolveListaVazia() {
        when(clienteRepository.listarPorOwner(ownerId)).thenReturn(List.of());

        assertThat(useCase.executar(ownerId)).isEmpty();
    }

    @Test
    @DisplayName("rejeita dono nulo")
    void rejeitaOwnerNulo() {
        assertThatNullPointerException().isThrownBy(() -> useCase.executar(null));
    }
}
