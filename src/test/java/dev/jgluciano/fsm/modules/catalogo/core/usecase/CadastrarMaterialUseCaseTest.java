package dev.jgluciano.fsm.modules.catalogo.core.usecase;

import dev.jgluciano.fsm.modules.catalogo.core.model.Material;
import dev.jgluciano.fsm.modules.catalogo.core.model.MaterialJaCadastradoException;
import dev.jgluciano.fsm.modules.catalogo.core.ports.MaterialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Fase 1b, RED. Ainda não existem: CadastrarMaterialUseCase (com Comando),
 * MaterialRepository (porta) e MaterialJaCadastradoException.
 */
@ExtendWith(MockitoExtension.class)
class CadastrarMaterialUseCaseTest {

    private final UUID ownerId = UUID.randomUUID();

    @Mock
    private MaterialRepository materialRepository;

    private CadastrarMaterialUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CadastrarMaterialUseCase(materialRepository);
    }

    private static BigDecimal bd(String valor) {
        return new BigDecimal(valor);
    }

    @Test
    @DisplayName("cadastra e salva o material do dono informado")
    void cadastraEPersisteMaterial() {
        when(materialRepository.existePorOwnerENome(ownerId, "Cabo 2,5mm")).thenReturn(false);
        when(materialRepository.salvar(any(Material.class))).thenAnswer(i -> i.getArgument(0));

        var material = useCase.executar(
                new CadastrarMaterialUseCase.Comando(ownerId, "Cabo 2,5mm", "m", bd("3.5")));

        var captor = ArgumentCaptor.forClass(Material.class);
        verify(materialRepository).salvar(captor.capture());
        assertThat(captor.getValue()).isEqualTo(material);
        assertThat(material.ownerId()).isEqualTo(ownerId);
        assertThat(material.nome()).isEqualTo("Cabo 2,5mm");
        assertThat(material.unidade()).isEqualTo("m");
        assertThat(material.custoUnitario()).isEqualTo(bd("3.50"));
    }

    @Test
    @DisplayName("rejeita material com nome já cadastrado para o dono")
    void rejeitaNomeDuplicado() {
        when(materialRepository.existePorOwnerENome(ownerId, "Cabo 2,5mm")).thenReturn(true);

        assertThatThrownBy(() -> useCase.executar(
                new CadastrarMaterialUseCase.Comando(ownerId, "Cabo 2,5mm", "m", bd("3.50"))))
                .isInstanceOf(MaterialJaCadastradoException.class)
                .hasMessageContaining("Cabo 2,5mm");

        verify(materialRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("verifica duplicidade com o nome já aparado")
    void verificaDuplicidadeComNomeAparado() {
        when(materialRepository.existePorOwnerENome(ownerId, "Cabo")).thenReturn(false);
        when(materialRepository.salvar(any(Material.class))).thenAnswer(i -> i.getArgument(0));

        var material = useCase.executar(
                new CadastrarMaterialUseCase.Comando(ownerId, "  Cabo ", "m", bd("1")));

        assertThat(material.nome()).isEqualTo("Cabo");
        verify(materialRepository).existePorOwnerENome(ownerId, "Cabo");
    }

    @Test
    @DisplayName("rejeita comando com valores nulos")
    void rejeitaComandoComNulos() {
        assertThatNullPointerException().isThrownBy(() ->
                new CadastrarMaterialUseCase.Comando(null, "Cabo", "m", bd("1")));
        assertThatNullPointerException().isThrownBy(() ->
                new CadastrarMaterialUseCase.Comando(ownerId, null, "m", bd("1")));
        assertThatNullPointerException().isThrownBy(() ->
                new CadastrarMaterialUseCase.Comando(ownerId, "Cabo", null, bd("1")));
        assertThatNullPointerException().isThrownBy(() ->
                new CadastrarMaterialUseCase.Comando(ownerId, "Cabo", "m", null));
    }
}
