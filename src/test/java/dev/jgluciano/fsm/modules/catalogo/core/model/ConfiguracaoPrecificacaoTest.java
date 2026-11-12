package dev.jgluciano.fsm.modules.catalogo.core.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Fase 1a, RED. Estas classes ainda não existem: ConfiguracaoPrecificacao e PrecificacaoInvalidaException.
 *
 * Regras:
 *   VHT           = (custos fixos mensais + pró-labore) / horas produtivas mensais
 *   Índice markup = 1 - (impostos% + taxas financeiras% + margem de lucro%) / 100
 * Precisão intermediária: escala 10, HALF_UP.
 */
class ConfiguracaoPrecificacaoTest {

    private static BigDecimal bd(String valor) {
        return new BigDecimal(valor);
    }

    private static ConfiguracaoPrecificacao config(String custosFixos, String proLabore, String horasProdutivas,
                                                   String impostos, String taxas, String margem) {
        return new ConfiguracaoPrecificacao(UUID.randomUUID(), bd(custosFixos), bd(proLabore),
                bd(horasProdutivas), bd(impostos), bd(taxas), bd(margem));
    }

    @Nested
    @DisplayName("Valor da Hora Técnica (VHT)")
    class ValorHoraTecnica {

        @Test
        @DisplayName("soma custos fixos e pró-labore e divide pelas horas produtivas")
        void somaCustosFixosEProLaboreDivididosPelasHorasProdutivas() {
            var config = config("3000.00", "5000.00", "160", "6", "4", "20");

            assertThat(config.valorHoraTecnica()).isEqualByComparingTo("50");
        }

        @Test
        @DisplayName("mantém 10 casas decimais, com HALF_UP, em dízima periódica")
        void mantemPrecisaoIntermediariaEmDezCasasDecimais() {
            var config = config("3000.00", "4000.00", "150", "6", "4", "20"); // 7000 / 150

            assertThat(config.valorHoraTecnica()).isEqualTo(bd("46.6666666667"));
        }

        @Test
        @DisplayName("aceita custos fixos e pró-labore zerados (VHT zero)")
        void aceitaCustosEProLaboreZerados() {
            var config = config("0", "0", "160", "6", "4", "20");

            assertThat(config.valorHoraTecnica()).isEqualByComparingTo("0");
        }
    }

    @Nested
    @DisplayName("Índice de Markup")
    class IndiceMarkup {

        @Test
        @DisplayName("1 - (impostos + taxas + margem) / 100")
        void umMenosSomaDosPercentuaisDivididaPorCem() {
            var config = config("3000.00", "5000.00", "160", "6", "4", "20"); // 30%

            assertThat(config.indiceMarkup()).isEqualByComparingTo("0.70");
        }

        @Test
        @DisplayName("mantém 10 casas decimais")
        void mantemPrecisaoIntermediariaEmDezCasasDecimais() {
            var config = config("3000.00", "5000.00", "160", "6", "4", "20");

            assertThat(config.indiceMarkup()).isEqualTo(bd("0.7000000000"));
        }

        @Test
        @DisplayName("aceita percentuais fracionados")
        void aceitaPercentuaisFracionados() {
            var config = config("3000.00", "5000.00", "160", "6.5", "3.25", "15.25"); // 25%

            assertThat(config.indiceMarkup()).isEqualByComparingTo("0.75");
        }

        @Test
        @DisplayName("percentuais zerados resultam em índice 1")
        void percentuaisZeradosResultamEmIndiceUm() {
            var config = config("3000.00", "5000.00", "160", "0", "0", "0");

            assertThat(config.indiceMarkup()).isEqualByComparingTo("1");
        }

        @Test
        @DisplayName("limite: soma de 99,99% ainda é válida (índice 0,0001)")
        void somaDeNoventaENoveVirgulaNoventaENoveEhValida() {
            var config = config("3000.00", "5000.00", "160", "30", "30", "39.99");

            assertThat(config.indiceMarkup()).isEqualByComparingTo("0.0001");
        }
    }

    @Nested
    @DisplayName("Validações")
    class Validacoes {

        @ParameterizedTest(name = "[{index}] rejeita {6}")
        @CsvSource({
                "-0.01, 5000, 160, 6, 4, 20, custos fixos negativos",
                "3000, -0.01, 160, 6, 4, 20, pró-labore negativo",
                "3000, 5000, 0, 6, 4, 20, horas produtivas zero",
                "3000, 5000, -1, 6, 4, 20, horas produtivas negativas",
                "3000, 5000, 160, -0.01, 4, 20, impostos negativos",
                "3000, 5000, 160, 6, -0.01, 20, taxas financeiras negativas",
                "3000, 5000, 160, 6, 4, -0.01, margem de lucro negativa",
                "3000, 5000, 160, 50, 30, 20, soma dos percentuais igual a 100",
                "3000, 5000, 160, 50, 30, 20.01, soma dos percentuais acima de 100"
        })
        void rejeitaValoresInvalidos(String custosFixos, String proLabore, String horasProdutivas,
                                     String impostos, String taxas, String margem, String descricao) {
            String trecho = trechoEsperadoDaMensagem(descricao);

            assertThatThrownBy(() -> config(custosFixos, proLabore, horasProdutivas, impostos, taxas, margem))
                    .isInstanceOf(PrecificacaoInvalidaException.class)
                    .hasMessageContainingIgnoringCase(trecho);
        }

        private String trechoEsperadoDaMensagem(String descricao) {
            if (descricao.startsWith("custos fixos")) return "custos fixos";
            if (descricao.startsWith("pró-labore")) return "pró-labore";
            if (descricao.startsWith("horas produtivas")) return "horas produtivas";
            if (descricao.startsWith("impostos")) return "impostos";
            if (descricao.startsWith("taxas financeiras")) return "taxas financeiras";
            if (descricao.startsWith("margem de lucro")) return "margem de lucro";
            return "soma dos percentuais";
        }

        @Test
        @DisplayName("rejeita dono (ownerId) nulo")
        void rejeitaOwnerNulo() {
            assertThatNullPointerException().isThrownBy(() ->
                    new ConfiguracaoPrecificacao(null, bd("3000"), bd("5000"), bd("160"), bd("6"), bd("4"), bd("20")));
        }

        @Test
        @DisplayName("rejeita valores monetários e percentuais nulos")
        void rejeitaValoresNulos() {
            var owner = UUID.randomUUID();

            assertThatNullPointerException().isThrownBy(() ->
                    new ConfiguracaoPrecificacao(owner, null, bd("5000"), bd("160"), bd("6"), bd("4"), bd("20")));
            assertThatNullPointerException().isThrownBy(() ->
                    new ConfiguracaoPrecificacao(owner, bd("3000"), bd("5000"), null, bd("6"), bd("4"), bd("20")));
            assertThatNullPointerException().isThrownBy(() ->
                    new ConfiguracaoPrecificacao(owner, bd("3000"), bd("5000"), bd("160"), bd("6"), bd("4"), null));
        }
    }
}
