package dev.jgluciano.fsm.modules.orcamento.core.usecase;

import dev.jgluciano.fsm.modules.orcamento.core.model.Orcamento;
import dev.jgluciano.fsm.modules.orcamento.core.model.OrcamentoNaoEncontradoException;
import dev.jgluciano.fsm.modules.orcamento.core.usecase.BuscarOrcamentoUseCase.Comando;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BuscarOrcamentoUseCaseTest {

    private static final UUID OWNER = UUID.randomUUID();

    private OrcamentoRepositoryEmMemoria repositorio;
    private BuscarOrcamentoUseCase useCase;

    @BeforeEach
    void preparar() {
        repositorio = new OrcamentoRepositoryEmMemoria();
        useCase = new BuscarOrcamentoUseCase(repositorio);
    }

    @Test
    void devolveOOrcamentoDoDono() {
        var salvo = repositorio.salvar(Orcamento.novo(OWNER, UUID.randomUUID()));

        assertThat(useCase.executar(new Comando(OWNER, salvo.id()))).isEqualTo(salvo);
    }

    @Test
    void falhaQuandoNaoExiste() {
        var idInexistente = UUID.randomUUID();

        assertThatThrownBy(() -> useCase.executar(new Comando(OWNER, idInexistente)))
                .isInstanceOf(OrcamentoNaoEncontradoException.class)
                .message().containsIgnoringCase("não encontrado");
    }

    @Test
    void orcamentoDeOutroDonoContaComoNaoEncontrado() {
        var deOutro = repositorio.salvar(Orcamento.novo(UUID.randomUUID(), UUID.randomUUID()));

        assertThatThrownBy(() -> useCase.executar(new Comando(OWNER, deOutro.id())))
                .isInstanceOf(OrcamentoNaoEncontradoException.class);
    }

    @Test
    void argumentosNulosGeramNullPointerException() {
        assertThatThrownBy(() -> new Comando(null, UUID.randomUUID())).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Comando(OWNER, null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> useCase.executar(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new BuscarOrcamentoUseCase(null)).isInstanceOf(NullPointerException.class);
    }
}
