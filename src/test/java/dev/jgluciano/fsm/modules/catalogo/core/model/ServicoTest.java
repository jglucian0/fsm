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
 * Fase 1c, RED. Ainda não existe: Servico.
 *
 * Regras: nome obrigatório (aparado, até 120 caracteres); horas padrão > 0,
 * normalizadas para escala 2, HALF_UP.
 */
class ServicoTest {

    private final UUID ownerId = UUID.randomUUID();

    private static BigDecimal bd(String valor) {
        return new BigDecimal(valor);
    }

    @Test
    @DisplayName("cria serviço válido com id gerado")
    void criaServicoValido() {
        var servico = Servico.novo(ownerId, "Instalação de tomada", bd("1.5"));

        assertThat(servico.id()).isNotNull();
        assertThat(servico.ownerId()).isEqualTo(ownerId);
        assertThat(servico.nome()).isEqualTo("Instalação de tomada");
        assertThat(servico.horasPadrao()).isEqualTo(bd("1.50"));
    }

    @Test
    @DisplayName("cada serviço novo recebe um id diferente")
    void geraIdsDiferentes() {
        var a = Servico.novo(ownerId, "Troca de disjuntor", bd("1"));
        var b = Servico.novo(ownerId, "Troca de disjuntor", bd("1"));

        assertThat(a.id()).isNotEqualTo(b.id());
    }

    @ParameterizedTest(name = "[{index}] {0} -> {1}")
    @CsvSource({"2.505, 2.51", "2, 2.00", "1.504, 1.50"})
    @DisplayName("normaliza as horas padrão para escala 2 com HALF_UP")
    void normalizaHorasParaEscalaDois(String informado, String esperado) {
        var servico = Servico.novo(ownerId, "Instalação de chuveiro", bd(informado));

        assertThat(servico.horasPadrao()).isEqualTo(bd(esperado));
    }

    @Test
    @DisplayName("apara espaços do nome")
    void aparaEspacosDoNome() {
        var servico = Servico.novo(ownerId, "  Troca de fiação  ", bd("4"));

        assertThat(servico.nome()).isEqualTo("Troca de fiação");
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-0.01", "-1"})
    @DisplayName("rejeita horas padrão zero ou negativas")
    void rejeitaHorasZeroOuNegativas(String horas) {
        assertThatThrownBy(() -> Servico.novo(ownerId, "Instalação", bd(horas)))
                .isInstanceOf(CatalogoInvalidoException.class)
                .message().containsIgnoringCase("horas");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t"})
    @DisplayName("rejeita nome em branco")
    void rejeitaNomeEmBranco(String nome) {
        assertThatThrownBy(() -> Servico.novo(ownerId, nome, bd("1")))
                .isInstanceOf(CatalogoInvalidoException.class)
                .message().containsIgnoringCase("nome");
    }

    @Test
    @DisplayName("aceita nome com exatamente 120 caracteres")
    void aceitaNomeNoLimite() {
        var nome = "a".repeat(120);

        assertThat(Servico.novo(ownerId, nome, bd("1")).nome()).hasSize(120);
    }

    @Test
    @DisplayName("rejeita nome com mais de 120 caracteres")
    void rejeitaNomeAcimaDoLimite() {
        var nome = "a".repeat(121);

        assertThatThrownBy(() -> Servico.novo(ownerId, nome, bd("1")))
                .isInstanceOf(CatalogoInvalidoException.class)
                .message().containsIgnoringCase("nome");
    }

    @Test
    @DisplayName("rejeita valores nulos")
    void rejeitaNulos() {
        assertThatNullPointerException().isThrownBy(() -> Servico.novo(null, "Instalação", bd("1")));
        assertThatNullPointerException().isThrownBy(() -> Servico.novo(ownerId, null, bd("1")));
        assertThatNullPointerException().isThrownBy(() -> Servico.novo(ownerId, "Instalação", null));
    }
}
