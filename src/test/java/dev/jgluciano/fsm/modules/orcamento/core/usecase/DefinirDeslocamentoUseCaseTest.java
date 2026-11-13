package dev.jgluciano.fsm.modules.orcamento.core.usecase;

import dev.jgluciano.fsm.modules.orcamento.core.model.OrcamentoInvalidoException;
import dev.jgluciano.fsm.modules.orcamento.core.model.OrcamentoNaoEncontradoException;
import dev.jgluciano.fsm.modules.orcamento.core.model.StatusOrcamentoInvalidoException;
import dev.jgluciano.fsm.modules.orcamento.core.usecase.DefinirDeslocamentoUseCase.Comando;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static dev.jgluciano.fsm.modules.orcamento.core.usecase.OrcamentosParaTeste.enviado;
import static dev.jgluciano.fsm.modules.orcamento.core.usecase.OrcamentosParaTeste.rascunho;
import static dev.jgluciano.fsm.modules.orcamento.core.usecase.OrcamentosParaTeste.rascunhoComItem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DefinirDeslocamentoUseCaseTest {

    private static final UUID OWNER = UUID.randomUUID();

    private OrcamentoRepositoryEmMemoria repositorio;
    private DefinirDeslocamentoUseCase useCase;

    @BeforeEach
    void preparar() {
        repositorio = new OrcamentoRepositoryEmMemoria();
        useCase = new DefinirDeslocamentoUseCase(repositorio);
    }

    @Test
    void defineODeslocamentoEPersiste() {
        var salvo = repositorio.salvar(rascunhoComItem(OWNER, "100.00"));

        var resultado = useCase.executar(new Comando(OWNER, salvo.id(), new BigDecimal("25.50")));

        assertThat(resultado.deslocamento()).isEqualByComparingTo("25.50");
        assertThat(resultado.total()).isEqualByComparingTo("125.50");
        assertThat(repositorio.buscarPorId(OWNER, salvo.id())).contains(resultado);
    }

    @Test
    void deslocamentoNegativoNaoAlteraNada() {
        var salvo = repositorio.salvar(rascunho(OWNER));
        var salvamentos = repositorio.totalDeSalvamentos;

        assertThatThrownBy(() -> useCase.executar(new Comando(OWNER, salvo.id(), new BigDecimal("-1.00"))))
                .isInstanceOf(OrcamentoInvalidoException.class);
        assertThat(repositorio.totalDeSalvamentos).isEqualTo(salvamentos);
    }

    @Test
    void reducaoQueQuebraODescontoERejeitada() {
        var base = rascunhoComItem(OWNER, "10.00").comDeslocamento(new BigDecimal("20.00"))
                .comDesconto(new BigDecimal("25.00"));
        var salvo = repositorio.salvar(base);

        assertThatThrownBy(() -> useCase.executar(new Comando(OWNER, salvo.id(), BigDecimal.ZERO)))
                .isInstanceOf(OrcamentoInvalidoException.class);
    }

    @Test
    void foraDeRascunhoNaoPermiteAlterar() {
        var enviado = repositorio.salvar(enviado(OWNER));

        assertThatThrownBy(() -> useCase.executar(new Comando(OWNER, enviado.id(), BigDecimal.ONE)))
                .isInstanceOf(StatusOrcamentoInvalidoException.class);
    }

    @Test
    void orcamentoInexistenteOuDeOutroDonoNaoEncontrado() {
        var deOutro = repositorio.salvar(rascunho(UUID.randomUUID()));

        assertThatThrownBy(() -> useCase.executar(new Comando(OWNER, UUID.randomUUID(), BigDecimal.ONE)))
                .isInstanceOf(OrcamentoNaoEncontradoException.class);
        assertThatThrownBy(() -> useCase.executar(new Comando(OWNER, deOutro.id(), BigDecimal.ONE)))
                .isInstanceOf(OrcamentoNaoEncontradoException.class);
    }

    @Test
    void argumentosNulosGeramNullPointerException() {
        var id = UUID.randomUUID();
        assertThatThrownBy(() -> new Comando(null, id, BigDecimal.ONE)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Comando(OWNER, null, BigDecimal.ONE)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Comando(OWNER, id, null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> useCase.executar(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new DefinirDeslocamentoUseCase(null)).isInstanceOf(NullPointerException.class);
    }
}
