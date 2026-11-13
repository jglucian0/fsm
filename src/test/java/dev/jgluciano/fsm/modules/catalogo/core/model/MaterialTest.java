package dev.jgluciano.fsm.modules.catalogo.core.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Fase 1b, RED. Ainda não existem: Material e CatalogoInvalidoException.
 *
 * Regras: nome e unidade obrigatórios (aparados), nome com até 120 caracteres,
 * custo unitário >= 0 e normalizado para escala 2, HALF_UP.
 */
class MaterialTest {

    private final UUID ownerId = UUID.randomUUID();

    private static BigDecimal bd(String valor) {
        return new BigDecimal(valor);
    }

    @Test
    @DisplayName("cria material válido com id gerado")
    void criaMaterialValido() {
        var material = Material.novo(ownerId, "Cabo flexível 2,5mm", "m", bd("3.50"));

        assertThat(material.id()).isNotNull();
        assertThat(material.ownerId()).isEqualTo(ownerId);
        assertThat(material.nome()).isEqualTo("Cabo flexível 2,5mm");
        assertThat(material.unidade()).isEqualTo("m");
        assertThat(material.custoUnitario()).isEqualTo(bd("3.50"));
    }

    @Test
    @DisplayName("cada material novo recebe um id diferente")
    void geraIdsDiferentes() {
        var a = Material.novo(ownerId, "Disjuntor 20A", "un", bd("18.90"));
        var b = Material.novo(ownerId, "Disjuntor 20A", "un", bd("18.90"));

        assertThat(a.id()).isNotEqualTo(b.id());
    }

    @ParameterizedTest(name = "[{index}] {0} -> {1}")
    @CsvSource({"10.005, 10.01", "2, 2.00", "3.504, 3.50"})
    @DisplayName("normaliza o custo unitário para escala 2 com HALF_UP")
    void normalizaCustoParaEscalaDois(String informado, String esperado) {
        var material = Material.novo(ownerId, "Fita isolante", "un", bd(informado));

        assertThat(material.custoUnitario()).isEqualTo(bd(esperado));
    }

    @Test
    @DisplayName("apara espaços do nome e da unidade")
    void aparaEspacos() {
        var material = Material.novo(ownerId, "  Disjuntor 20A  ", "  un ", bd("18.90"));

        assertThat(material.nome()).isEqualTo("Disjuntor 20A");
        assertThat(material.unidade()).isEqualTo("un");
    }

    @Test
    @DisplayName("aceita custo unitário zero")
    void aceitaCustoZero() {
        var material = Material.novo(ownerId, "Brinde", "un", BigDecimal.ZERO);

        assertThat(material.custoUnitario()).isEqualTo(bd("0.00"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-0.01", "-1"})
    @DisplayName("rejeita custo unitário negativo")
    void rejeitaCustoNegativo(String valor) {
        assertThatThrownBy(() -> Material.novo(ownerId, "Cabo", "m", bd(valor)))
                .isInstanceOf(CatalogoInvalidoException.class)
                .message().containsIgnoringCase("custo");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t"})
    @DisplayName("rejeita nome em branco")
    void rejeitaNomeEmBranco(String nome) {
        assertThatThrownBy(() -> Material.novo(ownerId, nome, "m", bd("1")))
                .isInstanceOf(CatalogoInvalidoException.class)
                .message().containsIgnoringCase("nome");
    }

    @Test
    @DisplayName("aceita nome com exatamente 120 caracteres")
    void aceitaNomeNoLimite() {
        var nome = "a".repeat(120);

        assertThat(Material.novo(ownerId, nome, "m", bd("1")).nome()).hasSize(120);
    }

    @Test
    @DisplayName("rejeita nome com mais de 120 caracteres")
    void rejeitaNomeAcimaDoLimite() {
        var nome = "a".repeat(121);

        assertThatThrownBy(() -> Material.novo(ownerId, nome, "m", bd("1")))
                .isInstanceOf(CatalogoInvalidoException.class)
                .message().containsIgnoringCase("nome");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    @DisplayName("rejeita unidade em branco")
    void rejeitaUnidadeEmBranco(String unidade) {
        assertThatThrownBy(() -> Material.novo(ownerId, "Cabo", unidade, bd("1")))
                .isInstanceOf(CatalogoInvalidoException.class)
                .message().containsIgnoringCase("unidade");
    }

    @Test
    @DisplayName("rejeita valores nulos")
    void rejeitaNulos() {
        assertThatNullPointerException().isThrownBy(() -> Material.novo(null, "Cabo", "m", bd("1")));
        assertThatNullPointerException().isThrownBy(() -> Material.novo(ownerId, null, "m", bd("1")));
        assertThatNullPointerException().isThrownBy(() -> Material.novo(ownerId, "Cabo", null, bd("1")));
        assertThatNullPointerException().isThrownBy(() -> Material.novo(ownerId, "Cabo", "m", null));
    }
}
