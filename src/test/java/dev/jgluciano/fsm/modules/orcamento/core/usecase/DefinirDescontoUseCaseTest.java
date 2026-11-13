package dev.jgluciano.fsm.modules.orcamento.core.usecase;

import dev.jgluciano.fsm.modules.orcamento.core.model.OrcamentoInvalidoException;
import dev.jgluciano.fsm.modules.orcamento.core.model.OrcamentoNaoEncontradoException;
import dev.jgluciano.fsm.modules.orcamento.core.model.StatusOrcamentoInvalidoException;
import dev.jgluciano.fsm.modules.orcamento.core.usecase.DefinirDescontoUseCase.Comando;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static dev.jgluciano.fsm.modules.orcamento.core.usecase.OrcamentosParaTeste.enviado;
import static dev.jgluciano.fsm.modules.orcamento.core.usecase.OrcamentosParaTeste.rascunho;
import static dev.jgluciano.fsm.modules.orcamento.core.usecase.OrcamentosParaTeste.rascunhoComItem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DefinirDescontoUseCaseTest {

    private static final UUID OWNER = UUID.randomUUID();

    private OrcamentoRepositoryEmMemoria repositorio;
    private DefinirDescontoUseCase useCase;

    @BeforeEach
    void preparar() {
        repositorio = new OrcamentoRepositoryEmMemoria();
        useCase = new DefinirDescontoUseCase(repositorio);
    }

    @Test
    void defineODescontoEPersiste() {
        var salvo = repositorio.salvar(rascunhoComItem(OWNER, "100.00"));

        var resultado = useCase.executar(new Comando(OWNER, salvo.id(), new BigDecimal("15.00")));

        assertThat(resultado.desconto()).isEqualByComparingTo("15.00");
        assertThat(resultado.total()).isEqualByComparingTo("85.00");
        assertThat(repositorio.buscarPorId(OWNER, salvo.id())).contains(resultado);
    }

    @Test
    void descontoMaiorQueSubtotalMaisDeslocamentoNaoAlteraNada() {
        var salvo = repositorio.salvar(rascunhoComItem(OWNER, "10.00").comDeslocamento(new BigDecimal("5.00")));
        var salvamentos = repositorio.totalDeSalvamentos;

        assertThatThrownBy(() -> useCase.executar(new Comando(OWNER, salvo.id(), new BigDecimal("15.01"))))
                .isInstanceOf(OrcamentoInvalidoException.class);
        assertThat(repositorio.totalDeSalvamentos).isEqualTo(salvamentos);
    }

    @Test
    void descontoNegativoERejeitado() {
        var salvo = repositorio.salvar(rascunhoComItem(OWNER, "10.00"));

        assertThatThrownBy(() -> useCase.executar(new Comando(OWNER, salvo.id(), new BigDecimal("-1.00"))))
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
        assertThatThrownBy(() -> new DefinirDescontoUseCase(null)).isInstanceOf(NullPointerException.class);
    }
}
