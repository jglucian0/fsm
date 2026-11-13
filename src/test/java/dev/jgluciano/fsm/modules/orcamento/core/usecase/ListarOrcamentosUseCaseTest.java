package dev.jgluciano.fsm.modules.orcamento.core.usecase;

import dev.jgluciano.fsm.modules.orcamento.core.model.ItemOrcamento;
import dev.jgluciano.fsm.modules.orcamento.core.model.Orcamento;
import dev.jgluciano.fsm.modules.orcamento.core.model.StatusOrcamento;
import dev.jgluciano.fsm.modules.orcamento.core.model.TipoItem;
import dev.jgluciano.fsm.modules.orcamento.core.usecase.ListarOrcamentosUseCase.Comando;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ListarOrcamentosUseCaseTest {

    private static final UUID OWNER = UUID.randomUUID();

    private OrcamentoRepositoryEmMemoria repositorio;
    private ListarOrcamentosUseCase useCase;

    @BeforeEach
    void preparar() {
        repositorio = new OrcamentoRepositoryEmMemoria();
        useCase = new ListarOrcamentosUseCase(repositorio);
    }

    @Test
    void semFiltroListaTodosOsOrcamentosDoDono() {
        var rascunho = repositorio.salvar(Orcamento.novo(OWNER, UUID.randomUUID()));
        var enviado = repositorio.salvar(enviado());

        assertThat(useCase.executar(new Comando(OWNER, null))).containsExactly(rascunho, enviado);
    }

    @Test
    void comFiltroListaSoOsDoStatusPedido() {
        repositorio.salvar(Orcamento.novo(OWNER, UUID.randomUUID()));
        var enviado = repositorio.salvar(enviado());

        assertThat(useCase.executar(new Comando(OWNER, StatusOrcamento.ENVIADO))).containsExactly(enviado);
        assertThat(useCase.executar(new Comando(OWNER, StatusOrcamento.APROVADO))).isEmpty();
    }

    @Test
    void naoMostraOrcamentosDeOutroDono() {
        repositorio.salvar(Orcamento.novo(UUID.randomUUID(), UUID.randomUUID()));

        assertThat(useCase.executar(new Comando(OWNER, null))).isEmpty();
    }

    @Test
    void argumentosNulosGeramNullPointerException() {
        assertThatThrownBy(() -> new Comando(null, null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> useCase.executar(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new ListarOrcamentosUseCase(null)).isInstanceOf(NullPointerException.class);
    }

    private static Orcamento enviado() {
        return Orcamento.novo(OWNER, UUID.randomUUID())
                .adicionarItem(new ItemOrcamento("Tomada", TipoItem.MATERIAL, BigDecimal.ONE, new BigDecimal("10.00")))
                .enviar();
    }
}
