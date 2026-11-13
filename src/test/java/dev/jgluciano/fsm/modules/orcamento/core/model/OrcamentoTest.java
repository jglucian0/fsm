package dev.jgluciano.fsm.modules.orcamento.core.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.UnaryOperator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Fase 3a, RED. Ainda não existem: Orcamento, StatusOrcamento e StatusOrcamentoInvalidoException
 * (além de ItemOrcamento, TipoItem e OrcamentoInvalidoException, ver ItemOrcamentoTest).
 *
 * Regras (ADR 0011): total = subtotal + deslocamento - desconto. Desconto fixo, nunca maior que subtotal + deslocamento.
 * Estados: RASCUNHO -> ENVIADO (com pelo menos um item) -> APROVADO -> FINALIZADO, ou ENVIADO -> REJEITADO.
 * REJEITADO e FINALIZADO são finais. Itens, deslocamento e desconto só mudam em RASCUNHO.
 * O orçamento é imutável: cada operação devolve um novo objeto.
 */
class OrcamentoTest {

    private final UUID ownerId = UUID.randomUUID();
    private final UUID clienteId = UUID.randomUUID();

    private static ItemOrcamento item(String quantidade, String valorUnitario) {
        return new ItemOrcamento("Item", TipoItem.SERVICO, new BigDecimal(quantidade), new BigDecimal(valorUnitario));
    }

    private static BigDecimal valor(String valor) {
        return new BigDecimal(valor);
    }

    private Orcamento rascunhoComItem() {
        return Orcamento.novo(ownerId, clienteId).adicionarItem(item("2", "150"));
    }

    private Orcamento emStatus(StatusOrcamento status) {
        return new Orcamento(UUID.randomUUID(), ownerId, clienteId, status, List.of(item("2", "150")),
                valor("0"), valor("0"));
    }

    @Test
    @DisplayName("orçamento novo nasce em RASCUNHO, vazio e com valores zerados")
    void criaOrcamentoNovo() {
        var orcamento = Orcamento.novo(ownerId, clienteId);

        assertThat(orcamento.id()).isNotNull();
        assertThat(orcamento.ownerId()).isEqualTo(ownerId);
        assertThat(orcamento.clienteId()).isEqualTo(clienteId);
        assertThat(orcamento.status()).isEqualTo(StatusOrcamento.RASCUNHO);
        assertThat(orcamento.itens()).isEmpty();
        assertThat(orcamento.deslocamento()).isEqualTo(valor("0.00"));
        assertThat(orcamento.desconto()).isEqualTo(valor("0.00"));
        assertThat(orcamento.subtotal()).isEqualTo(valor("0.00"));
        assertThat(orcamento.total()).isEqualTo(valor("0.00"));
    }

    @Test
    @DisplayName("cada orçamento novo recebe um id diferente")
    void geraIdsDiferentes() {
        assertThat(Orcamento.novo(ownerId, clienteId).id()).isNotEqualTo(Orcamento.novo(ownerId, clienteId).id());
    }

    @Test
    @DisplayName("rejeita dono ou cliente nulos")
    void rejeitaDonoOuClienteNulos() {
        assertThatNullPointerException().isThrownBy(() -> Orcamento.novo(null, clienteId));
        assertThatNullPointerException().isThrownBy(() -> Orcamento.novo(ownerId, null));
    }

    // ---------------------------------------------------------------- itens

    @Test
    @DisplayName("adicionar item devolve um novo orçamento, mantém a ordem e não altera o original")
    void adicionaItem() {
        var vazio = Orcamento.novo(ownerId, clienteId);
        var primeiro = item("1", "10");
        var segundo = item("2", "20");

        var comItens = vazio.adicionarItem(primeiro).adicionarItem(segundo);

        assertThat(comItens.itens()).containsExactly(primeiro, segundo);
        assertThat(vazio.itens()).isEmpty();
        assertThat(comItens.id()).isEqualTo(vazio.id());
    }

    @Test
    @DisplayName("a lista de itens exposta não pode ser alterada por fora")
    void listaDeItensEhImutavel() {
        var orcamento = rascunhoComItem();

        assertThatThrownBy(() -> orcamento.itens().add(item("1", "1")))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("remover item por índice devolve novo orçamento sem aquele item")
    void removeItem() {
        var primeiro = item("1", "10");
        var segundo = item("2", "20");
        var orcamento = Orcamento.novo(ownerId, clienteId).adicionarItem(primeiro).adicionarItem(segundo);

        var semPrimeiro = orcamento.removerItem(0);

        assertThat(semPrimeiro.itens()).containsExactly(segundo);
        assertThat(orcamento.itens()).hasSize(2);
    }

    @Test
    @DisplayName("remover item com índice inexistente é rejeitado")
    void rejeitaIndiceInexistente() {
        var orcamento = rascunhoComItem();

        assertThatThrownBy(() -> orcamento.removerItem(1))
                .isInstanceOf(OrcamentoInvalidoException.class)
                .message().containsIgnoringCase("item");
        assertThatThrownBy(() -> orcamento.removerItem(-1))
                .isInstanceOf(OrcamentoInvalidoException.class);
    }

    @Test
    @DisplayName("rejeita item nulo")
    void rejeitaItemNulo() {
        assertThatNullPointerException().isThrownBy(() -> Orcamento.novo(ownerId, clienteId).adicionarItem(null));
    }

    // --------------------------------------------------------------- totais

    @Test
    @DisplayName("subtotal soma os subtotais dos itens")
    void somaSubtotal() {
        var orcamento = Orcamento.novo(ownerId, clienteId)
                .adicionarItem(item("2", "150"))      // 300.00
                .adicionarItem(item("1.50", "10.01")); // 15.02

        assertThat(orcamento.subtotal()).isEqualTo(valor("315.02"));
        assertThat(orcamento.total()).isEqualTo(valor("315.02"));
    }

    @Test
    @DisplayName("total = subtotal + deslocamento - desconto")
    void calculaTotal() {
        var orcamento = Orcamento.novo(ownerId, clienteId)
                .adicionarItem(item("2", "150"))
                .adicionarItem(item("1.50", "10.01"))
                .comDeslocamento(valor("50"))
                .comDesconto(valor("15.02"));

        assertThat(orcamento.subtotal()).isEqualTo(valor("315.02"));
        assertThat(orcamento.deslocamento()).isEqualTo(valor("50.00"));
        assertThat(orcamento.desconto()).isEqualTo(valor("15.02"));
        assertThat(orcamento.total()).isEqualTo(valor("350.00"));
    }

    @Test
    @DisplayName("deslocamento e desconto são normalizados para escala 2 (HALF_UP)")
    void normalizaDeslocamentoEDesconto() {
        var orcamento = rascunhoComItem().comDeslocamento(valor("12.345")).comDesconto(valor("1.005"));

        assertThat(orcamento.deslocamento()).isEqualTo(valor("12.35"));
        assertThat(orcamento.desconto()).isEqualTo(valor("1.01"));
    }

    @Test
    @DisplayName("rejeita deslocamento negativo")
    void rejeitaDeslocamentoNegativo() {
        assertThatThrownBy(() -> rascunhoComItem().comDeslocamento(valor("-0.01")))
                .isInstanceOf(OrcamentoInvalidoException.class)
                .message().containsIgnoringCase("deslocamento");
    }

    @Test
    @DisplayName("rejeita desconto negativo")
    void rejeitaDescontoNegativo() {
        assertThatThrownBy(() -> rascunhoComItem().comDesconto(valor("-0.01")))
                .isInstanceOf(OrcamentoInvalidoException.class)
                .message().containsIgnoringCase("desconto");
    }

    @Test
    @DisplayName("desconto igual a subtotal + deslocamento é aceito (total zero)")
    void aceitaDescontoIgualAoTotalBruto() {
        var orcamento = rascunhoComItem().comDeslocamento(valor("20")).comDesconto(valor("320"));

        assertThat(orcamento.total()).isEqualTo(valor("0.00"));
    }

    @Test
    @DisplayName("rejeita desconto maior que subtotal + deslocamento")
    void rejeitaDescontoMaiorQueOTotalBruto() {
        assertThatThrownBy(() -> rascunhoComItem().comDeslocamento(valor("20")).comDesconto(valor("320.01")))
                .isInstanceOf(OrcamentoInvalidoException.class)
                .message().containsIgnoringCase("desconto");
    }

    @Test
    @DisplayName("rejeita desconto em orçamento sem itens e sem deslocamento")
    void rejeitaDescontoSemBase() {
        assertThatThrownBy(() -> Orcamento.novo(ownerId, clienteId).comDesconto(valor("0.01")))
                .isInstanceOf(OrcamentoInvalidoException.class)
                .message().containsIgnoringCase("desconto");
    }

    @Test
    @DisplayName("reduzir o deslocamento que deixaria o desconto acima do limite é rejeitado")
    void rejeitaReduzirDeslocamentoAcimaDoDesconto() {
        var orcamento = rascunhoComItem().comDeslocamento(valor("50")).comDesconto(valor("340"));

        assertThatThrownBy(() -> orcamento.comDeslocamento(valor("10")))
                .isInstanceOf(OrcamentoInvalidoException.class)
                .message().containsIgnoringCase("desconto");
    }

    @Test
    @DisplayName("remover item que deixaria o desconto acima do limite é rejeitado")
    void rejeitaRemoverItemAcimaDoDesconto() {
        var orcamento = Orcamento.novo(ownerId, clienteId)
                .adicionarItem(item("1", "100"))
                .adicionarItem(item("1", "100"))
                .comDesconto(valor("150"));

        assertThatThrownBy(() -> orcamento.removerItem(0))
                .isInstanceOf(OrcamentoInvalidoException.class)
                .message().containsIgnoringCase("desconto");
    }

    @Test
    @DisplayName("rejeita deslocamento e desconto nulos")
    void rejeitaValoresNulos() {
        assertThatNullPointerException().isThrownBy(() -> rascunhoComItem().comDeslocamento(null));
        assertThatNullPointerException().isThrownBy(() -> rascunhoComItem().comDesconto(null));
    }

    // -------------------------------------------------------------- estados

    @Test
    @DisplayName("fluxo feliz: RASCUNHO, ENVIADO, APROVADO, FINALIZADO")
    void fluxoFeliz() {
        var rascunho = rascunhoComItem();
        var enviado = rascunho.enviar();
        var aprovado = enviado.aprovar();
        var finalizado = aprovado.finalizar();

        assertThat(rascunho.status()).isEqualTo(StatusOrcamento.RASCUNHO);
        assertThat(enviado.status()).isEqualTo(StatusOrcamento.ENVIADO);
        assertThat(aprovado.status()).isEqualTo(StatusOrcamento.APROVADO);
        assertThat(finalizado.status()).isEqualTo(StatusOrcamento.FINALIZADO);
    }

    @Test
    @DisplayName("ENVIADO pode ser REJEITADO")
    void enviadoPodeSerRejeitado() {
        assertThat(rascunhoComItem().enviar().rejeitar().status()).isEqualTo(StatusOrcamento.REJEITADO);
    }

    @Test
    @DisplayName("a transição preserva identidade, itens e valores")
    void transicaoPreservaDados() {
        var rascunho = rascunhoComItem().comDeslocamento(valor("40")).comDesconto(valor("10"));

        var enviado = rascunho.enviar();

        assertThat(enviado.id()).isEqualTo(rascunho.id());
        assertThat(enviado.ownerId()).isEqualTo(ownerId);
        assertThat(enviado.clienteId()).isEqualTo(clienteId);
        assertThat(enviado.itens()).isEqualTo(rascunho.itens());
        assertThat(enviado.total()).isEqualTo(valor("330.00"));
        assertThat(rascunho.status()).isEqualTo(StatusOrcamento.RASCUNHO);
    }

    @Test
    @DisplayName("não envia orçamento sem itens")
    void naoEnviaSemItens() {
        assertThatThrownBy(() -> Orcamento.novo(ownerId, clienteId).enviar())
                .isInstanceOf(OrcamentoInvalidoException.class)
                .message().containsIgnoringCase("pelo menos um item");
    }

    @ParameterizedTest(name = "{0} não permite {1}")
    @CsvSource({
            "RASCUNHO,  aprovar",
            "RASCUNHO,  rejeitar",
            "RASCUNHO,  finalizar",
            "ENVIADO,   enviar",
            "ENVIADO,   finalizar",
            "APROVADO,  enviar",
            "APROVADO,  aprovar",
            "APROVADO,  rejeitar",
            "REJEITADO, enviar",
            "REJEITADO, aprovar",
            "REJEITADO, rejeitar",
            "REJEITADO, finalizar",
            "FINALIZADO, enviar",
            "FINALIZADO, aprovar",
            "FINALIZADO, rejeitar",
            "FINALIZADO, finalizar"
    })
    @DisplayName("rejeita transição inválida e cita o status atual")
    void rejeitaTransicaoInvalida(StatusOrcamento status, String operacao) {
        var orcamento = emStatus(status);

        assertThatThrownBy(() -> aplicar(orcamento, operacao))
                .isInstanceOf(StatusOrcamentoInvalidoException.class)
                .message().containsIgnoringCase(status.name());
    }

    @ParameterizedTest
    @EnumSource(value = StatusOrcamento.class, names = {"ENVIADO", "APROVADO", "REJEITADO", "FINALIZADO"})
    @DisplayName("itens, deslocamento e desconto só mudam em RASCUNHO")
    void naoEditaForaDeRascunho(StatusOrcamento status) {
        var orcamento = emStatus(status);

        assertThatThrownBy(() -> orcamento.adicionarItem(item("1", "10")))
                .isInstanceOf(StatusOrcamentoInvalidoException.class);
        assertThatThrownBy(() -> orcamento.removerItem(0))
                .isInstanceOf(StatusOrcamentoInvalidoException.class);
        assertThatThrownBy(() -> orcamento.comDeslocamento(valor("10")))
                .isInstanceOf(StatusOrcamentoInvalidoException.class);
        assertThatThrownBy(() -> orcamento.comDesconto(valor("10")))
                .isInstanceOf(StatusOrcamentoInvalidoException.class);
    }

    private static Orcamento aplicar(Orcamento orcamento, String operacao) {
        UnaryOperator<Orcamento> acao = switch (operacao) {
            case "enviar" -> Orcamento::enviar;
            case "aprovar" -> Orcamento::aprovar;
            case "rejeitar" -> Orcamento::rejeitar;
            case "finalizar" -> Orcamento::finalizar;
            default -> throw new IllegalArgumentException(operacao);
        };
        return acao.apply(orcamento);
    }

    // ----------------------------------------------- reconstituição (banco)

    @Test
    @DisplayName("o construtor canônico reconstitui o orçamento e normaliza os valores")
    void reconstitui() {
        var id = UUID.randomUUID();

        var orcamento = new Orcamento(id, ownerId, clienteId, StatusOrcamento.APROVADO,
                List.of(item("2", "150")), valor("5"), valor("10"));

        assertThat(orcamento.id()).isEqualTo(id);
        assertThat(orcamento.status()).isEqualTo(StatusOrcamento.APROVADO);
        assertThat(orcamento.deslocamento()).isEqualTo(valor("5.00"));
        assertThat(orcamento.total()).isEqualTo(valor("295.00"));
    }

    @Test
    @DisplayName("o construtor copia a lista de itens recebida")
    void copiaListaDeItens() {
        var itens = new ArrayList<>(List.of(item("1", "10")));
        var orcamento = new Orcamento(UUID.randomUUID(), ownerId, clienteId, StatusOrcamento.RASCUNHO, itens,
                valor("0"), valor("0"));

        itens.add(item("1", "20"));

        assertThat(orcamento.itens()).hasSize(1);
    }

    @ParameterizedTest
    @EnumSource(value = StatusOrcamento.class, names = {"ENVIADO", "APROVADO", "REJEITADO", "FINALIZADO"})
    @DisplayName("o construtor rejeita orçamento fora de RASCUNHO sem itens")
    void rejeitaForaDeRascunhoSemItens(StatusOrcamento status) {
        assertThatThrownBy(() -> new Orcamento(UUID.randomUUID(), ownerId, clienteId, status, List.of(),
                valor("0"), valor("0")))
                .isInstanceOf(OrcamentoInvalidoException.class)
                .message().containsIgnoringCase("pelo menos um item");
    }

    @Test
    @DisplayName("o construtor rejeita desconto acima do limite e valores negativos")
    void construtorRejeitaValoresInvalidos() {
        var itens = List.of(item("1", "100"));

        assertThatThrownBy(() -> new Orcamento(UUID.randomUUID(), ownerId, clienteId, StatusOrcamento.RASCUNHO,
                itens, valor("0"), valor("100.01")))
                .isInstanceOf(OrcamentoInvalidoException.class)
                .message().containsIgnoringCase("desconto");
        assertThatThrownBy(() -> new Orcamento(UUID.randomUUID(), ownerId, clienteId, StatusOrcamento.RASCUNHO,
                itens, valor("-1"), valor("0")))
                .isInstanceOf(OrcamentoInvalidoException.class)
                .message().containsIgnoringCase("deslocamento");
    }

    @Test
    @DisplayName("o construtor rejeita argumentos nulos")
    void construtorRejeitaNulos() {
        var itens = List.of(item("1", "100"));
        var id = UUID.randomUUID();

        assertThatNullPointerException().isThrownBy(() -> new Orcamento(null, ownerId, clienteId,
                StatusOrcamento.RASCUNHO, itens, valor("0"), valor("0")));
        assertThatNullPointerException().isThrownBy(() -> new Orcamento(id, null, clienteId,
                StatusOrcamento.RASCUNHO, itens, valor("0"), valor("0")));
        assertThatNullPointerException().isThrownBy(() -> new Orcamento(id, ownerId, null,
                StatusOrcamento.RASCUNHO, itens, valor("0"), valor("0")));
        assertThatNullPointerException().isThrownBy(() -> new Orcamento(id, ownerId, clienteId,
                null, itens, valor("0"), valor("0")));
        assertThatNullPointerException().isThrownBy(() -> new Orcamento(id, ownerId, clienteId,
                StatusOrcamento.RASCUNHO, null, valor("0"), valor("0")));
        assertThatNullPointerException().isThrownBy(() -> new Orcamento(id, ownerId, clienteId,
                StatusOrcamento.RASCUNHO, itens, null, valor("0")));
        assertThatNullPointerException().isThrownBy(() -> new Orcamento(id, ownerId, clienteId,
                StatusOrcamento.RASCUNHO, itens, valor("0"), null));
    }
}
