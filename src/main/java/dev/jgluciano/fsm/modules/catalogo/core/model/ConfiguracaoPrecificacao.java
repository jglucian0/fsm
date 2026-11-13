package dev.jgluciano.fsm.modules.catalogo.core.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.UUID;

import static dev.jgluciano.fsm.modules.catalogo.core.model.Validacoes.exigirNaoNegativo;

/**
 * Parâmetros de precificação do profissional (ADR 0005).
 * Percentuais em pontos: 6 significa 6%.
 */
public record ConfiguracaoPrecificacao(
        UUID ownerId,
        BigDecimal custosFixosMensais,
        BigDecimal proLabore,
        BigDecimal horasProdutivasMensais,
        BigDecimal impostosPercentual,
        BigDecimal taxasFinanceirasPercentual,
        BigDecimal margemLucroPercentual) {

    /** Escala dos cálculos intermediários. */
    public static final int ESCALA_CALCULO = 10;
    public static final RoundingMode ARREDONDAMENTO = RoundingMode.HALF_UP;

    private static final BigDecimal CEM = BigDecimal.valueOf(100);

    public ConfiguracaoPrecificacao {
        Objects.requireNonNull(ownerId, "ownerId");
        Objects.requireNonNull(custosFixosMensais, "custosFixosMensais");
        Objects.requireNonNull(proLabore, "proLabore");
        Objects.requireNonNull(horasProdutivasMensais, "horasProdutivasMensais");
        Objects.requireNonNull(impostosPercentual, "impostosPercentual");
        Objects.requireNonNull(taxasFinanceirasPercentual, "taxasFinanceirasPercentual");
        Objects.requireNonNull(margemLucroPercentual, "margemLucroPercentual");

        exigirNaoNegativo(custosFixosMensais, "Custos fixos");
        exigirNaoNegativo(proLabore, "Pró-labore");
        if (horasProdutivasMensais.signum() <= 0) {
            throw new PrecificacaoInvalidaException("Horas produtivas mensais devem ser maiores que zero");
        }
        exigirNaoNegativo(impostosPercentual, "Impostos");
        exigirNaoNegativo(taxasFinanceirasPercentual, "Taxas financeiras");
        exigirNaoNegativo(margemLucroPercentual, "Margem de lucro");

        if (somaPercentuais(impostosPercentual, taxasFinanceirasPercentual, margemLucroPercentual)
                .compareTo(CEM) >= 0) {
            throw new PrecificacaoInvalidaException("A soma dos percentuais deve ser menor que 100");
        }
    }

    /** VHT = (custos fixos + pró-labore) / horas produtivas, em escala 10. */
    public BigDecimal valorHoraTecnica() {
        return custosFixosMensais.add(proLabore)
                .divide(horasProdutivasMensais, ESCALA_CALCULO, ARREDONDAMENTO);
    }

    /** Índice de markup = 1 - (impostos% + taxas% + margem%) / 100, em escala 10. */
    public BigDecimal indiceMarkup() {
        var soma = somaPercentuais(impostosPercentual, taxasFinanceirasPercentual, margemLucroPercentual);
        return BigDecimal.ONE.subtract(soma.divide(CEM, ESCALA_CALCULO, ARREDONDAMENTO));
    }

    private static BigDecimal somaPercentuais(BigDecimal a, BigDecimal b, BigDecimal c) {
        return a.add(b).add(c);
    }
}
