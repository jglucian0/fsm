package dev.jgluciano.fsm.modules.catalogo.infrastructure.web;

import dev.jgluciano.fsm.modules.catalogo.core.model.ConfiguracaoPrecificacao;

import java.math.BigDecimal;

public record ConfiguracaoPrecificacaoResponse(
        BigDecimal custosFixosMensais,
        BigDecimal proLabore,
        BigDecimal horasProdutivasMensais,
        BigDecimal impostosPercentual,
        BigDecimal taxasFinanceirasPercentual,
        BigDecimal margemLucroPercentual) {

    static ConfiguracaoPrecificacaoResponse de(ConfiguracaoPrecificacao c) {
        return new ConfiguracaoPrecificacaoResponse(c.custosFixosMensais(), c.proLabore(),
                c.horasProdutivasMensais(), c.impostosPercentual(), c.taxasFinanceirasPercentual(),
                c.margemLucroPercentual());
    }
}
