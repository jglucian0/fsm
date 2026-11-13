package dev.jgluciano.fsm.modules.orcamento.core.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Fase 3a, RED. Ainda não existem: ItemOrcamento, TipoItem e OrcamentoInvalidoException.
 *
 * Regras (ADR 0011): item é um snapshot (descrição, tipo, quantidade, valor unitário). Descrição aparada, até 200.
 * Quantidade e valor unitário são normalizados para escala 2 (HALF_UP); quantidade > 0 depois de normalizar;
 * valor unitário >= 0. Subtotal = quantidade x valor unitário, arredondado uma vez (HALF_UP, escala 2).
 */
class ItemOrcamentoTest {

    private static ItemOrcamento item(String descricao, String quantidade, String valorUnitario) {
        return new ItemOrcamento(descricao, TipoItem.SERVICO, new BigDecimal(quantidade),
                new BigDecimal(valorUnitario));
    }

    @Test
    @DisplayName("cria item válido, apara a descrição e normaliza valores para escala 2")
    void criaItemValido() {
        var item = new ItemOrcamento("  Instalação de tomada ", TipoItem.SERVICO, new BigDecimal("2"),
                new BigDecimal("150"));

        assertThat(item.descricao()).isEqualTo("Instalação de tomada");
        assertThat(item.tipo()).isEqualTo(TipoItem.SERVICO);
        assertThat(item.quantidade()).isEqualTo(new BigDecimal("2.00"));
        assertThat(item.valorUnitario()).isEqualTo(new BigDecimal("150.00"));
    }

    @Test
    @DisplayName("normaliza quantidade e valor unitário com HALF_UP")
    void normalizaComHalfUp() {
        var item = item("Cabo flexível 2,5mm", "1.505", "10.005");

        assertThat(item.quantidade()).isEqualTo(new BigDecimal("1.51"));
        assertThat(item.valorUnitario()).isEqualTo(new BigDecimal("10.01"));
    }

    @ParameterizedTest(name = "{0} x {1} = {2}")
    @CsvSource({
            "2.00, 150.00, 300.00",
            "1.50, 10.01, 15.02",
            "0.50, 7.00, 3.50",
            "3.00, 33.33, 99.99"
    })
    @DisplayName("subtotal é quantidade x valor unitário, arredondado uma vez (HALF_UP, escala 2)")
    void calculaSubtotal(String quantidade, String valorUnitario, String esperado) {
        assertThat(item("Item", quantidade, valorUnitario).subtotal()).isEqualTo(new BigDecimal(esperado));
    }

    @Test
    @DisplayName("aceita valor unitário zero (cortesia) e tipo MATERIAL")
    void aceitaValorZero() {
        var item = new ItemOrcamento("Fita isolante (cortesia)", TipoItem.MATERIAL, BigDecimal.ONE, BigDecimal.ZERO);

        assertThat(item.subtotal()).isEqualTo(new BigDecimal("0.00"));
        assertThat(item.tipo()).isEqualTo(TipoItem.MATERIAL);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   "})
    @DisplayName("rejeita descrição em branco")
    void rejeitaDescricaoEmBranco(String descricao) {
        assertThatThrownBy(() -> item(descricao, "1", "10"))
                .isInstanceOf(OrcamentoInvalidoException.class)
                .message().containsIgnoringCase("descrição");
    }

    @Test
    @DisplayName("descrição aceita 200 caracteres e rejeita 201")
    void limitaTamanhoDaDescricao() {
        assertThat(item("a".repeat(200), "1", "10").descricao()).hasSize(200);

        assertThatThrownBy(() -> item("a".repeat(201), "1", "10"))
                .isInstanceOf(OrcamentoInvalidoException.class)
                .message().containsIgnoringCase("descrição");
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "0.004"})
    @DisplayName("rejeita quantidade zero, negativa ou que arredonda para zero")
    void rejeitaQuantidadeInvalida(String quantidade) {
        assertThatThrownBy(() -> item("Item", quantidade, "10"))
                .isInstanceOf(OrcamentoInvalidoException.class)
                .message().containsIgnoringCase("quantidade");
    }

    @Test
    @DisplayName("rejeita valor unitário negativo")
    void rejeitaValorNegativo() {
        assertThatThrownBy(() -> item("Item", "1", "-0.01"))
                .isInstanceOf(OrcamentoInvalidoException.class)
                .message().containsIgnoringCase("valor unitário");
    }

    @Test
    @DisplayName("rejeita argumentos nulos")
    void rejeitaNulos() {
        assertThatNullPointerException().isThrownBy(
                () -> new ItemOrcamento(null, TipoItem.SERVICO, BigDecimal.ONE, BigDecimal.TEN));
        assertThatNullPointerException().isThrownBy(
                () -> new ItemOrcamento("Item", null, BigDecimal.ONE, BigDecimal.TEN));
        assertThatNullPointerException().isThrownBy(
                () -> new ItemOrcamento("Item", TipoItem.SERVICO, null, BigDecimal.TEN));
        assertThatNullPointerException().isThrownBy(
                () -> new ItemOrcamento("Item", TipoItem.SERVICO, BigDecimal.ONE, null));
    }
}
