package dev.jgluciano.fsm.modules.catalogo.core.usecase;

import dev.jgluciano.fsm.modules.catalogo.core.model.Servico;
import dev.jgluciano.fsm.modules.catalogo.core.model.ServicoJaCadastradoException;
import dev.jgluciano.fsm.modules.catalogo.core.ports.ServicoRepository;
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
 * Fase 1c, RED. Ainda não existem: CadastrarServicoUseCase (com Comando),
 * ServicoRepository (porta) e ServicoJaCadastradoException.
 */
@ExtendWith(MockitoExtension.class)
class CadastrarServicoUseCaseTest {

    private final UUID ownerId = UUID.randomUUID();

    @Mock
    private ServicoRepository servicoRepository;

    private CadastrarServicoUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CadastrarServicoUseCase(servicoRepository);
    }

    private static BigDecimal bd(String valor) {
        return new BigDecimal(valor);
    }

    @Test
    @DisplayName("cadastra e salva o serviço do dono informado")
    void cadastraEPersisteServico() {
        when(servicoRepository.existePorOwnerENome(ownerId, "Instalação de tomada")).thenReturn(false);
        when(servicoRepository.salvar(any(Servico.class))).thenAnswer(i -> i.getArgument(0));

        var servico = useCase.executar(
                new CadastrarServicoUseCase.Comando(ownerId, "Instalação de tomada", bd("1.5")));

        var captor = ArgumentCaptor.forClass(Servico.class);
        verify(servicoRepository).salvar(captor.capture());
        assertThat(captor.getValue()).isEqualTo(servico);
        assertThat(servico.ownerId()).isEqualTo(ownerId);
        assertThat(servico.nome()).isEqualTo("Instalação de tomada");
        assertThat(servico.horasPadrao()).isEqualTo(bd("1.50"));
    }

    @Test
    @DisplayName("rejeita serviço com nome já cadastrado para o dono")
    void rejeitaNomeDuplicado() {
        when(servicoRepository.existePorOwnerENome(ownerId, "Instalação de tomada")).thenReturn(true);

        assertThatThrownBy(() -> useCase.executar(
                new CadastrarServicoUseCase.Comando(ownerId, "Instalação de tomada", bd("1"))))
                .isInstanceOf(ServicoJaCadastradoException.class)
                .hasMessageContaining("Instalação de tomada");

        verify(servicoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("verifica duplicidade com o nome já aparado")
    void verificaDuplicidadeComNomeAparado() {
        when(servicoRepository.existePorOwnerENome(ownerId, "Troca")).thenReturn(false);
        when(servicoRepository.salvar(any(Servico.class))).thenAnswer(i -> i.getArgument(0));

        var servico = useCase.executar(new CadastrarServicoUseCase.Comando(ownerId, "  Troca ", bd("1")));

        assertThat(servico.nome()).isEqualTo("Troca");
        verify(servicoRepository).existePorOwnerENome(ownerId, "Troca");
    }

    @Test
    @DisplayName("rejeita comando com valores nulos")
    void rejeitaComandoComNulos() {
        assertThatNullPointerException().isThrownBy(() ->
                new CadastrarServicoUseCase.Comando(null, "Troca", bd("1")));
        assertThatNullPointerException().isThrownBy(() ->
                new CadastrarServicoUseCase.Comando(ownerId, null, bd("1")));
        assertThatNullPointerException().isThrownBy(() ->
                new CadastrarServicoUseCase.Comando(ownerId, "Troca", null));
    }
}
