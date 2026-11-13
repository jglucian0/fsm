package dev.jgluciano.fsm.modules.orcamento.core.usecase;

import dev.jgluciano.fsm.modules.orcamento.core.model.ClienteDoOrcamentoInexistenteException;
import dev.jgluciano.fsm.modules.orcamento.core.model.StatusOrcamento;
import dev.jgluciano.fsm.modules.orcamento.core.usecase.CriarOrcamentoUseCase.Comando;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CriarOrcamentoUseCaseTest {

    private static final UUID OWNER = UUID.randomUUID();
    private static final UUID CLIENTE = UUID.randomUUID();

    private OrcamentoRepositoryEmMemoria repositorio;
    private ClienteConsultaFake clientes;
    private CriarOrcamentoUseCase useCase;

    @BeforeEach
    void preparar() {
        repositorio = new OrcamentoRepositoryEmMemoria();
        clientes = new ClienteConsultaFake();
        useCase = new CriarOrcamentoUseCase(repositorio, clientes);
    }

    @Test
    void criaOrcamentoEmRascunhoVazioParaClienteExistente() {
        clientes.cadastrar(OWNER, CLIENTE);

        var orcamento = useCase.executar(new Comando(OWNER, CLIENTE));

        assertThat(orcamento.ownerId()).isEqualTo(OWNER);
        assertThat(orcamento.clienteId()).isEqualTo(CLIENTE);
        assertThat(orcamento.status()).isEqualTo(StatusOrcamento.RASCUNHO);
        assertThat(orcamento.itens()).isEmpty();
        assertThat(orcamento.total()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void persisteOOrcamentoCriado() {
        clientes.cadastrar(OWNER, CLIENTE);

        var orcamento = useCase.executar(new Comando(OWNER, CLIENTE));

        assertThat(repositorio.buscarPorId(OWNER, orcamento.id())).contains(orcamento);
    }

    @Test
    void rejeitaClienteInexistenteSemSalvarNada() {
        var comando = new Comando(OWNER, CLIENTE);

        assertThatThrownBy(() -> useCase.executar(comando))
                .isInstanceOf(ClienteDoOrcamentoInexistenteException.class)
                .message().containsIgnoringCase("cliente");
        assertThat(repositorio.totalDeSalvamentos).isZero();
    }

    @Test
    void rejeitaClienteQuePertenceAOutroDono() {
        var outroDono = UUID.randomUUID();
        clientes.cadastrar(outroDono, CLIENTE);

        assertThatThrownBy(() -> useCase.executar(new Comando(OWNER, CLIENTE)))
                .isInstanceOf(ClienteDoOrcamentoInexistenteException.class);
        assertThat(repositorio.totalDeSalvamentos).isZero();
    }

    @Test
    void argumentosNulosGeramNullPointerException() {
        assertThatThrownBy(() -> new Comando(null, CLIENTE)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Comando(OWNER, null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> useCase.executar(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new CriarOrcamentoUseCase(null, clientes)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new CriarOrcamentoUseCase(repositorio, null)).isInstanceOf(NullPointerException.class);
    }
}
