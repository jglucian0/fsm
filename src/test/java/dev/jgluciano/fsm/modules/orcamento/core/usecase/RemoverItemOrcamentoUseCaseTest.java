package dev.jgluciano.fsm.modules.orcamento.core.usecase;

import dev.jgluciano.fsm.modules.orcamento.core.model.OrcamentoInvalidoException;
import dev.jgluciano.fsm.modules.orcamento.core.model.OrcamentoNaoEncontradoException;
import dev.jgluciano.fsm.modules.orcamento.core.model.StatusOrcamentoInvalidoException;
import dev.jgluciano.fsm.modules.orcamento.core.usecase.RemoverItemOrcamentoUseCase.Comando;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static dev.jgluciano.fsm.modules.orcamento.core.usecase.OrcamentosParaTeste.enviado;
import static dev.jgluciano.fsm.modules.orcamento.core.usecase.OrcamentosParaTeste.rascunho;
import static dev.jgluciano.fsm.modules.orcamento.core.usecase.OrcamentosParaTeste.rascunhoComItem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoverItemOrcamentoUseCaseTest {

    private static final UUID OWNER = UUID.randomUUID();

    private OrcamentoRepositoryEmMemoria repositorio;
    private RemoverItemOrcamentoUseCase useCase;

    @BeforeEach
    void preparar() {
        repositorio = new OrcamentoRepositoryEmMemoria();
        useCase = new RemoverItemOrcamentoUseCase(repositorio);
    }

    @Test
    void removeOItemDaPosicaoEPersiste() {
        var salvo = repositorio.salvar(rascunhoComItem(OWNER, "10.00"));

        var resultado = useCase.executar(new Comando(OWNER, salvo.id(), 0));

        assertThat(resultado.itens()).isEmpty();
        assertThat(repositorio.buscarPorId(OWNER, salvo.id())).contains(resultado);
    }

    @Test
    void indiceInexistenteNaoAlteraNada() {
        var salvo = repositorio.salvar(rascunhoComItem(OWNER, "10.00"));
        var salvamentos = repositorio.totalDeSalvamentos;

        assertThatThrownBy(() -> useCase.executar(new Comando(OWNER, salvo.id(), 5)))
                .isInstanceOf(OrcamentoInvalidoException.class);
        assertThat(repositorio.totalDeSalvamentos).isEqualTo(salvamentos);
    }

    @Test
    void remocaoQueQuebraODescontoERejeitada() {
        var comDesconto = rascunhoComItem(OWNER, "10.00").comDesconto(new BigDecimal("10.00"));
        var salvo = repositorio.salvar(comDesconto);

        assertThatThrownBy(() -> useCase.executar(new Comando(OWNER, salvo.id(), 0)))
                .isInstanceOf(OrcamentoInvalidoException.class);
        assertThat(repositorio.buscarPorId(OWNER, salvo.id()).orElseThrow().itens()).hasSize(1);
    }

    @Test
    void foraDeRascunhoNaoPermiteRemover() {
        var enviado = repositorio.salvar(enviado(OWNER));

        assertThatThrownBy(() -> useCase.executar(new Comando(OWNER, enviado.id(), 0)))
                .isInstanceOf(StatusOrcamentoInvalidoException.class);
    }

    @Test
    void orcamentoInexistenteOuDeOutroDonoNaoEncontrado() {
        var deOutro = repositorio.salvar(rascunhoComItem(UUID.randomUUID(), "10.00"));

        assertThatThrownBy(() -> useCase.executar(new Comando(OWNER, UUID.randomUUID(), 0)))
                .isInstanceOf(OrcamentoNaoEncontradoException.class);
        assertThatThrownBy(() -> useCase.executar(new Comando(OWNER, deOutro.id(), 0)))
                .isInstanceOf(OrcamentoNaoEncontradoException.class);
    }

    @Test
    void argumentosNulosGeramNullPointerException() {
        assertThatThrownBy(() -> new Comando(null, UUID.randomUUID(), 0)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Comando(OWNER, null, 0)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> useCase.executar(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new RemoverItemOrcamentoUseCase(null)).isInstanceOf(NullPointerException.class);
    }
}
