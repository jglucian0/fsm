package dev.jgluciano.fsm.modules.catalogo.core.usecase;

import dev.jgluciano.fsm.modules.catalogo.core.model.Material;
import dev.jgluciano.fsm.modules.catalogo.core.ports.MaterialRepository;
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

/** Fase 1b, RED. Ainda não existe: ListarMateriaisUseCase. */
@ExtendWith(MockitoExtension.class)
class ListarMateriaisUseCaseTest {

    private final UUID ownerId = UUID.randomUUID();

    @Mock
    private MaterialRepository materialRepository;

    private ListarMateriaisUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ListarMateriaisUseCase(materialRepository);
    }

    @Test
    @DisplayName("lista os materiais do dono informado")
    void listaMateriaisDoOwner() {
        var materiais = List.of(
                Material.novo(ownerId, "Cabo 2,5mm", "m", new BigDecimal("3.50")),
                Material.novo(ownerId, "Disjuntor 20A", "un", new BigDecimal("18.90")));
        when(materialRepository.listarPorOwner(ownerId)).thenReturn(materiais);

        var resultado = useCase.executar(ownerId);

        assertThat(resultado).containsExactlyElementsOf(materiais);
        verify(materialRepository).listarPorOwner(ownerId);
        verifyNoMoreInteractions(materialRepository);
    }

    @Test
    @DisplayName("devolve lista vazia quando não há materiais")
    void devolveListaVazia() {
        when(materialRepository.listarPorOwner(ownerId)).thenReturn(List.of());

        assertThat(useCase.executar(ownerId)).isEmpty();
    }

    @Test
    @DisplayName("rejeita dono nulo")
    void rejeitaOwnerNulo() {
        assertThatNullPointerException().isThrownBy(() -> useCase.executar(null));
    }
}
