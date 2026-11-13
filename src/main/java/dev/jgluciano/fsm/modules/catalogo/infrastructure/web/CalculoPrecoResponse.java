package dev.jgluciano.fsm.modules.catalogo.infrastructure.web;

import dev.jgluciano.fsm.modules.catalogo.core.usecase.CalcularPrecoServicoUseCase;

import java.math.BigDecimal;

public record CalculoPrecoResponse(BigDecimal valorHoraTecnica, BigDecimal custoDireto,
                                   BigDecimal indiceMarkup, BigDecimal precoFinal) {

    static CalculoPrecoResponse de(CalcularPrecoServicoUseCase.Resultado r) {
        return new CalculoPrecoResponse(r.valorHoraTecnica(), r.custoDireto(), r.indiceMarkup(), r.precoFinal());
    }
}
