package dev.jgluciano.fsm.modules.catalogo.core.usecase;

import dev.jgluciano.fsm.modules.catalogo.core.model.ConfiguracaoPrecificacao;
import dev.jgluciano.fsm.modules.catalogo.core.model.PrecificacaoInvalidaException;
import dev.jgluciano.fsm.modules.catalogo.core.ports.ConfiguracaoPrecificacaoRepository;
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
 * Fase 1d, RED. Ainda não existe: DefinirConfiguracaoPrecificacaoUseCase (com Comando) nem
 * ConfiguracaoPrecificacaoRepository#salvar. Definir = criar ou substituir a configuração do dono.
 */
@ExtendWith(MockitoExtension.class)
class DefinirConfiguracaoPrecificacaoUseCaseTest {

    private final UUID ownerId = UUID.randomUUID();

    @Mock
    private ConfiguracaoPrecificacaoRepository repository;

    private DefinirConfiguracaoPrecificacaoUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new DefinirConfiguracaoPrecificacaoUseCase(repository);
    }

    private static BigDecimal bd(String valor) {
        return new BigDecimal(valor);
    }

    private DefinirConfiguracaoPrecificacaoUseCase.Comando comando(String custosFixos, String proLabore,
                                                                    String horas, String impostos,
                                                                    String taxas, String margem) {
        return new DefinirConfiguracaoPrecificacaoUseCase.Comando(ownerId, bd(custosFixos), bd(proLabore),
                bd(horas), bd(impostos), bd(taxas), bd(margem));
    }

    @Test
    @DisplayName("salva a configuração do dono informado e devolve o resultado salvo")
    void salvaConfiguracao() {
        when(repository.salvar(any(ConfiguracaoPrecificacao.class))).thenAnswer(i -> i.getArgument(0));

        var configuracao = useCase.executar(comando("3000.00", "5000.00", "160", "6", "4", "20"));

        var captor = ArgumentCaptor.forClass(ConfiguracaoPrecificacao.class);
        verify(repository).salvar(captor.capture());
        assertThat(captor.getValue()).isEqualTo(configuracao);
        assertThat(configuracao.ownerId()).isEqualTo(ownerId);
        assertThat(configuracao.valorHoraTecnica()).isEqualByComparingTo("50");
        assertThat(configuracao.indiceMarkup()).isEqualByComparingTo("0.70");
    }

    @Test
    @DisplayName("não salva quando a configuração viola uma invariante")
    void naoSalvaConfiguracaoInvalida() {
        assertThatThrownBy(() -> useCase.executar(comando("3000.00", "5000.00", "160", "50", "30", "20")))
                .isInstanceOf(PrecificacaoInvalidaException.class)
                .message().containsIgnoringCase("soma dos percentuais");

        verify(repository, never()).salvar(any());
    }

    @Test
    @DisplayName("rejeita comando com valores nulos")
    void rejeitaComandoComNulos() {
        assertThatNullPointerException().isThrownBy(() ->
                new DefinirConfiguracaoPrecificacaoUseCase.Comando(null, bd("1"), bd("1"), bd("160"),
                        bd("1"), bd("1"), bd("1")));
        assertThatNullPointerException().isThrownBy(() ->
                new DefinirConfiguracaoPrecificacaoUseCase.Comando(ownerId, null, bd("1"), bd("160"),
                        bd("1"), bd("1"), bd("1")));
        assertThatNullPointerException().isThrownBy(() ->
                new DefinirConfiguracaoPrecificacaoUseCase.Comando(ownerId, bd("1"), bd("1"), bd("160"),
                        bd("1"), bd("1"), null));
    }
}
