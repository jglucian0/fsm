package dev.jgluciano.fsm.modules.catalogo.core.usecase;

import dev.jgluciano.fsm.modules.catalogo.core.model.ConfiguracaoPrecificacao;
import dev.jgluciano.fsm.modules.catalogo.core.model.ConfiguracaoPrecificacaoNaoEncontradaException;
import dev.jgluciano.fsm.modules.catalogo.core.ports.ConfiguracaoPrecificacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/** Fase 1d, RED. Ainda não existe: ObterConfiguracaoPrecificacaoUseCase. */
@ExtendWith(MockitoExtension.class)
class ObterConfiguracaoPrecificacaoUseCaseTest {

    private final UUID ownerId = UUID.randomUUID();

    @Mock
    private ConfiguracaoPrecificacaoRepository repository;

    private ObterConfiguracaoPrecificacaoUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ObterConfiguracaoPrecificacaoUseCase(repository);
    }

    @Test
    @DisplayName("devolve a configuração do dono")
    void devolveConfiguracao() {
        var configuracao = new ConfiguracaoPrecificacao(ownerId, new BigDecimal("3000"), new BigDecimal("5000"),
                new BigDecimal("160"), new BigDecimal("6"), new BigDecimal("4"), new BigDecimal("20"));
        when(repository.buscarPorOwner(ownerId)).thenReturn(Optional.of(configuracao));

        assertThat(useCase.executar(ownerId)).isEqualTo(configuracao);
    }

    @Test
    @DisplayName("lança exceção quando o dono não tem configuração")
    void falhaQuandoNaoHaConfiguracao() {
        when(repository.buscarPorOwner(ownerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.executar(ownerId))
                .isInstanceOf(ConfiguracaoPrecificacaoNaoEncontradaException.class)
                .hasMessageContaining(ownerId.toString());
    }

    @Test
    @DisplayName("rejeita dono nulo")
    void rejeitaOwnerNulo() {
        assertThatNullPointerException().isThrownBy(() -> useCase.executar(null));
    }
}
