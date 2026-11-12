package dev.jgluciano.fsm.modules.catalogo.core.usecase;

import dev.jgluciano.fsm.modules.catalogo.core.model.ConfiguracaoPrecificacao;
import dev.jgluciano.fsm.modules.catalogo.core.model.ConfiguracaoPrecificacaoNaoEncontradaException;
import dev.jgluciano.fsm.modules.catalogo.core.model.PrecificacaoInvalidaException;
import dev.jgluciano.fsm.modules.catalogo.core.ports.ConfiguracaoPrecificacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Fase 1a, RED. Ainda não existem: CalcularPrecoServicoUseCase (com Comando e Resultado),
 * ConfiguracaoPrecificacaoRepository (porta) e ConfiguracaoPrecificacaoNaoEncontradaException.
 *
 * Regras:
 *   Custo direto = horas estimadas x VHT + custo de materiais + despesas extras
 *   Preço final  = custo direto / índice de markup
 * Valores monetários expostos: BigDecimal, escala 2, HALF_UP.
 * Cálculo interno: escala 10, arredondando só na saída (ver ADR 0005).
 */
@ExtendWith(MockitoExtension.class)
class CalcularPrecoServicoUseCaseTest {

    private final UUID ownerId = UUID.randomUUID();

    @Mock
    private ConfiguracaoPrecificacaoRepository configuracaoRepository;

    private CalcularPrecoServicoUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CalcularPrecoServicoUseCase(configuracaoRepository);
    }

    // ---------- helpers ----------

    private static BigDecimal bd(String valor) {
        return new BigDecimal(valor);
    }

    /** Configuração base: VHT = 8000 / 160 = 50,00 | índice = 1 - 30% = 0,70 */
    private void configuracaoBase() {
        configuracaoDoOwner("3000.00", "5000.00", "160", "6", "4", "20");
    }

    private void configuracaoDoOwner(String custosFixos, String proLabore, String horasProdutivas,
                                     String impostos, String taxas, String margem) {
        var config = new ConfiguracaoPrecificacao(ownerId, bd(custosFixos), bd(proLabore), bd(horasProdutivas),
                bd(impostos), bd(taxas), bd(margem));
        when(configuracaoRepository.buscarPorOwner(ownerId)).thenReturn(Optional.of(config));
    }

    private CalcularPrecoServicoUseCase.Resultado calcular(String horas, String materiais, String extras) {
        return useCase.executar(new CalcularPrecoServicoUseCase.Comando(ownerId, bd(horas), bd(materiais), bd(extras)));
    }

    // ---------- cenários principais ----------

    @Test
    @DisplayName("cenário base: 4h x 50 + 100 + 50 = 350,00 -> preço 500,00")
    void calculaPrecoFinalCompleto() {
        configuracaoBase();

        var resultado = calcular("4", "100.00", "50.00");

        assertThat(resultado.custoDireto()).isEqualTo(bd("350.00"));
        assertThat(resultado.precoFinal()).isEqualTo(bd("500.00"));
    }

    @Test
    @DisplayName("resultado expõe VHT (escala 2), custo direto, índice e preço final")
    void resultadoExpoeTodosOsComponentesDoCalculo() {
        configuracaoBase();

        var resultado = calcular("4", "100.00", "50.00");

        assertThat(resultado.valorHoraTecnica()).isEqualTo(bd("50.00"));
        assertThat(resultado.custoDireto()).isEqualTo(bd("350.00"));
        assertThat(resultado.indiceMarkup()).isEqualByComparingTo("0.70");
        assertThat(resultado.precoFinal()).isEqualTo(bd("500.00"));
    }

    @Test
    @DisplayName("busca a configuração do dono informado e nada mais")
    void usaConfiguracaoDoOwnerInformado() {
        configuracaoBase();

        calcular("4", "100.00", "50.00");

        verify(configuracaoRepository).buscarPorOwner(ownerId);
        verifyNoMoreInteractions(configuracaoRepository);
    }

    @Test
    @DisplayName("só mão de obra: 2h x 50 = 100,00 -> 142,86 (arredonda)")
    void calculaApenasMaoDeObra() {
        configuracaoBase();

        var resultado = calcular("2", "0", "0");

        assertThat(resultado.custoDireto()).isEqualTo(bd("100.00"));
        assertThat(resultado.precoFinal()).isEqualTo(bd("142.86"));
    }

    @Test
    @DisplayName("só materiais, sem horas: 70,00 -> 100,00")
    void calculaApenasMateriais() {
        configuracaoBase();

        var resultado = calcular("0", "70.00", "0");

        assertThat(resultado.custoDireto()).isEqualTo(bd("70.00"));
        assertThat(resultado.precoFinal()).isEqualTo(bd("100.00"));
    }

    @Test
    @DisplayName("horas estimadas fracionadas: 2,5h x 50 + 100 + 50 = 275,00 -> 392,86")
    void aceitaHorasFracionadas() {
        configuracaoBase();

        var resultado = calcular("2.5", "100.00", "50.00");

        assertThat(resultado.custoDireto()).isEqualTo(bd("275.00"));
        assertThat(resultado.precoFinal()).isEqualTo(bd("392.86"));
    }

    @Test
    @DisplayName("percentuais fracionados: 25% no total -> 350,00 / 0,75 = 466,67")
    void aceitaPercentuaisFracionados() {
        configuracaoDoOwner("3000.00", "5000.00", "160", "6.5", "3.25", "15.25");

        var resultado = calcular("4", "100.00", "50.00");

        assertThat(resultado.precoFinal()).isEqualTo(bd("466.67"));
    }

    // ---------- casos limite ----------

    @Test
    @DisplayName("limite: tudo zero resulta em custo e preço 0,00 (escala 2)")
    void tudoZeroResultaEmPrecoZero() {
        configuracaoBase();

        var resultado = calcular("0", "0", "0");

        assertThat(resultado.custoDireto()).isEqualTo(bd("0.00"));
        assertThat(resultado.precoFinal()).isEqualTo(bd("0.00"));
    }

    @Test
    @DisplayName("limite: percentuais zerados -> preço igual ao custo direto")
    void percentuaisZeradosResultamEmPrecoIgualAoCusto() {
        configuracaoDoOwner("3000.00", "5000.00", "160", "0", "0", "0");

        var resultado = calcular("2", "10.00", "5.00");

        assertThat(resultado.custoDireto()).isEqualTo(bd("115.00"));
        assertThat(resultado.precoFinal()).isEqualTo(bd("115.00"));
    }

    @Test
    @DisplayName("limite: soma de 99,99% gera índice 0,0001 -> 1.000.000,00")
    void indiceMinimoGeraPrecoMuitoAlto() {
        configuracaoDoOwner("3000.00", "5000.00", "160", "30", "30", "39.99");

        var resultado = calcular("0", "100.00", "0");

        assertThat(resultado.precoFinal()).isEqualTo(bd("1000000.00"));
    }

    // ---------- arredondamento ----------

    @Test
    @DisplayName("HALF_UP: 10,02 / 0,80 = 12,525 -> 12,53 (HALF_EVEN daria 12,52)")
    void arredondaMeioParaCima() {
        configuracaoDoOwner("0", "0", "160", "6", "4", "10");

        var resultado = calcular("0", "10.02", "0");

        assertThat(resultado.precoFinal()).isEqualTo(bd("12.53"));
    }

    @Test
    @DisplayName("VHT em dízima não é arredondado antes de multiplicar pelas horas")
    void naoArredondaVhtAntesDeMultiplicar() {
        // VHT = 7000 / 150 = 46,6666... ; 3h = 140,00 (com VHT arredondado para 46,67 seria 140,01)
        configuracaoDoOwner("3000.00", "4000.00", "150", "0", "0", "10");

        var resultado = calcular("3", "0", "0");

        assertThat(resultado.valorHoraTecnica()).isEqualTo(bd("46.67"));
        assertThat(resultado.custoDireto()).isEqualTo(bd("140.00"));
        assertThat(resultado.precoFinal()).isEqualTo(bd("155.56")); // com VHT arredondado: 155,57
    }

    @Test
    @DisplayName("preço final usa o custo direto sem arredondar")
    void precoFinalUsaCustoDiretoSemArredondar() {
        // custo = 46,6666666667 (exibido 46,67) ; /0,9 = 51,85 ; com custo arredondado: 46,67 / 0,9 = 51,86
        configuracaoDoOwner("3000.00", "4000.00", "150", "0", "0", "10");

        var resultado = calcular("1", "0", "0");

        assertThat(resultado.custoDireto()).isEqualTo(bd("46.67"));
        assertThat(resultado.precoFinal()).isEqualTo(bd("51.85"));
    }

    // ---------- configuração ausente ----------

    @Test
    @DisplayName("lança exceção quando o dono não tem configuração de precificação")
    void falhaQuandoNaoHaConfiguracao() {
        when(configuracaoRepository.buscarPorOwner(ownerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> calcular("4", "100.00", "50.00"))
                .isInstanceOf(ConfiguracaoPrecificacaoNaoEncontradaException.class)
                .hasMessageContaining(ownerId.toString());
    }

    // ---------- validação do comando ----------

    @ParameterizedTest
    @ValueSource(strings = {"-0.01", "-1"})
    @DisplayName("rejeita horas estimadas negativas")
    void rejeitaHorasEstimadasNegativas(String valor) {
        assertThatThrownBy(() ->
                new CalcularPrecoServicoUseCase.Comando(ownerId, bd(valor), BigDecimal.ZERO, BigDecimal.ZERO))
                .isInstanceOf(PrecificacaoInvalidaException.class)
                .hasMessageContainingIgnoringCase("horas estimadas");
    }

    @ParameterizedTest
    @ValueSource(strings = {"-0.01", "-1"})
    @DisplayName("rejeita custo de materiais negativo")
    void rejeitaMateriaisNegativos(String valor) {
        assertThatThrownBy(() ->
                new CalcularPrecoServicoUseCase.Comando(ownerId, BigDecimal.ONE, bd(valor), BigDecimal.ZERO))
                .isInstanceOf(PrecificacaoInvalidaException.class)
                .hasMessageContainingIgnoringCase("materiais");
    }

    @ParameterizedTest
    @ValueSource(strings = {"-0.01", "-1"})
    @DisplayName("rejeita despesas extras negativas")
    void rejeitaDespesasExtrasNegativas(String valor) {
        assertThatThrownBy(() ->
                new CalcularPrecoServicoUseCase.Comando(ownerId, BigDecimal.ONE, BigDecimal.ZERO, bd(valor)))
                .isInstanceOf(PrecificacaoInvalidaException.class)
                .hasMessageContainingIgnoringCase("despesas extras");
    }

    @Test
    @DisplayName("rejeita comando com valores nulos")
    void rejeitaComandoComNulos() {
        assertThatNullPointerException().isThrownBy(() ->
                new CalcularPrecoServicoUseCase.Comando(null, BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ZERO));
        assertThatNullPointerException().isThrownBy(() ->
                new CalcularPrecoServicoUseCase.Comando(ownerId, null, BigDecimal.ZERO, BigDecimal.ZERO));
        assertThatNullPointerException().isThrownBy(() ->
                new CalcularPrecoServicoUseCase.Comando(ownerId, BigDecimal.ONE, null, BigDecimal.ZERO));
        assertThatNullPointerException().isThrownBy(() ->
                new CalcularPrecoServicoUseCase.Comando(ownerId, BigDecimal.ONE, BigDecimal.ZERO, null));
    }
}
