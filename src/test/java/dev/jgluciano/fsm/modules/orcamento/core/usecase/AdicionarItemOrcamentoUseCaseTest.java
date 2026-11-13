package dev.jgluciano.fsm.modules.orcamento.core.usecase;

import dev.jgluciano.fsm.modules.orcamento.core.model.OrcamentoInvalidoException;
import dev.jgluciano.fsm.modules.orcamento.core.model.OrcamentoNaoEncontradoException;
import dev.jgluciano.fsm.modules.orcamento.core.model.StatusOrcamentoInvalidoException;
import dev.jgluciano.fsm.modules.orcamento.core.model.TipoItem;
import dev.jgluciano.fsm.modules.orcamento.core.usecase.AdicionarItemOrcamentoUseCase.Comando;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static dev.jgluciano.fsm.modules.orcamento.core.usecase.OrcamentosParaTeste.enviado;
import static dev.jgluciano.fsm.modules.orcamento.core.usecase.OrcamentosParaTeste.rascunho;
import static dev.jgluciano.fsm.modules.orcamento.core.usecase.OrcamentosParaTeste.rascunhoComItem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AdicionarItemOrcamentoUseCaseTest {

    private static final UUID OWNER = UUID.randomUUID();

    private OrcamentoRepositoryEmMemoria repositorio;
    private AdicionarItemOrcamentoUseCase useCase;

    @BeforeEach
    void preparar() {
        repositorio = new OrcamentoRepositoryEmMemoria();
        useCase = new AdicionarItemOrcamentoUseCase(repositorio);
    }

    @Test
    void adicionaItemEPersiste() {
        var salvo = repositorio.salvar(rascunho(OWNER));

        var resultado = useCase.executar(comando(salvo.id(), "  Instalação de tomada ", "2", "50.00"));

        assertThat(resultado.itens()).hasSize(1);
        var item = resultado.itens().get(0);
        assertThat(item.descricao()).isEqualTo("Instalação de tomada");
        assertThat(item.tipo()).isEqualTo(TipoItem.SERVICO);
        assertThat(item.subtotal()).isEqualByComparingTo("100.00");
        assertThat(resultado.total()).isEqualByComparingTo("100.00");
        assertThat(repositorio.buscarPorId(OWNER, salvo.id())).contains(resultado);
    }

    @Test
    void itemInvalidoNaoAlteraNada() {
        var salvo = repositorio.salvar(rascunho(OWNER));
        var salvamentos = repositorio.totalDeSalvamentos;

        assertThatThrownBy(() -> useCase.executar(comando(salvo.id(), "Tomada", "0", "10.00")))
                .isInstanceOf(OrcamentoInvalidoException.class);
        assertThat(repositorio.totalDeSalvamentos).isEqualTo(salvamentos);
    }

    @Test
    void foraDeRascunhoNaoPermiteAdicionar() {
        var enviado = repositorio.salvar(enviado(OWNER));
        var salvamentos = repositorio.totalDeSalvamentos;

        assertThatThrownBy(() -> useCase.executar(comando(enviado.id(), "Tomada", "1", "10.00")))
                .isInstanceOf(StatusOrcamentoInvalidoException.class);
        assertThat(repositorio.totalDeSalvamentos).isEqualTo(salvamentos);
    }

    @Test
    void orcamentoInexistenteOuDeOutroDonoNaoEncontrado() {
        var deOutro = repositorio.salvar(rascunho(UUID.randomUUID()));

        assertThatThrownBy(() -> useCase.executar(comando(UUID.randomUUID(), "Tomada", "1", "10.00")))
                .isInstanceOf(OrcamentoNaoEncontradoException.class);
        assertThatThrownBy(() -> useCase.executar(comando(deOutro.id(), "Tomada", "1", "10.00")))
                .isInstanceOf(OrcamentoNaoEncontradoException.class);
    }

    @Test
    void argumentosNulosGeramNullPointerException() {
        var id = UUID.randomUUID();
        var qtd = BigDecimal.ONE;
        assertThatThrownBy(() -> new Comando(null, id, "x", TipoItem.SERVICO, qtd, qtd))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Comando(OWNER, null, "x", TipoItem.SERVICO, qtd, qtd))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Comando(OWNER, id, null, TipoItem.SERVICO, qtd, qtd))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Comando(OWNER, id, "x", null, qtd, qtd))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Comando(OWNER, id, "x", TipoItem.SERVICO, null, qtd))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Comando(OWNER, id, "x", TipoItem.SERVICO, qtd, null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> useCase.executar(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new AdicionarItemOrcamentoUseCase(null)).isInstanceOf(NullPointerException.class);
    }

    private static Comando comando(UUID orcamentoId, String descricao, String quantidade, String valorUnitario) {
        return new Comando(OWNER, orcamentoId, descricao, TipoItem.SERVICO, new BigDecimal(quantidade),
                new BigDecimal(valorUnitario));
    }
}
