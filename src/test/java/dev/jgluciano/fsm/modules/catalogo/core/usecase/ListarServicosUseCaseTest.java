package dev.jgluciano.fsm.modules.catalogo.core.usecase;

import dev.jgluciano.fsm.modules.catalogo.core.model.Servico;
import dev.jgluciano.fsm.modules.catalogo.core.ports.ServicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/** Fase 1c, RED. Ainda não existe: ListarServicosUseCase. */
@ExtendWith(MockitoExtension.class)
class ListarServicosUseCaseTest {

    private final UUID ownerId = UUID.randomUUID();

    @Mock
    private ServicoRepository servicoRepository;

    private ListarServicosUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ListarServicosUseCase(servicoRepository);
    }

    @Test
    @DisplayName("lista os serviços do dono informado")
    void listaServicosDoOwner() {
        var servicos = List.of(
                Servico.novo(ownerId, "Instalação de tomada", new BigDecimal("1.5")),
                Servico.novo(ownerId, "Troca de disjuntor", new BigDecimal("1")));
        when(servicoRepository.listarPorOwner(ownerId)).thenReturn(servicos);

        var resultado = useCase.executar(ownerId);

        assertThat(resultado).containsExactlyElementsOf(servicos);
        verify(servicoRepository).listarPorOwner(ownerId);
        verifyNoMoreInteractions(servicoRepository);
    }

    @Test
    @DisplayName("devolve lista vazia quando não há serviços")
    void devolveListaVazia() {
        when(servicoRepository.listarPorOwner(ownerId)).thenReturn(List.of());

        assertThat(useCase.executar(ownerId)).isEmpty();
    }

    @Test
    @DisplayName("rejeita dono nulo")
    void rejeitaOwnerNulo() {
        assertThatNullPointerException().isThrownBy(() -> useCase.executar(null));
    }
}
