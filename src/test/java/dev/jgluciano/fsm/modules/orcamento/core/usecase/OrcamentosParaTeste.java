package dev.jgluciano.fsm.modules.orcamento.core.usecase;

import dev.jgluciano.fsm.modules.orcamento.core.model.ItemOrcamento;
import dev.jgluciano.fsm.modules.orcamento.core.model.Orcamento;
import dev.jgluciano.fsm.modules.orcamento.core.model.TipoItem;

import java.math.BigDecimal;
import java.util.UUID;

/** Fábrica de orçamentos prontos para os testes dos casos de uso. */
final class OrcamentosParaTeste {

    private OrcamentosParaTeste() {
    }

    static Orcamento rascunho(UUID ownerId) {
        return Orcamento.novo(ownerId, UUID.randomUUID());
    }

    static Orcamento rascunhoComItem(UUID ownerId, String valorUnitario) {
        return rascunho(ownerId).adicionarItem(
                new ItemOrcamento("Tomada", TipoItem.MATERIAL, BigDecimal.ONE, new BigDecimal(valorUnitario)));
    }

    static Orcamento enviado(UUID ownerId) {
        return rascunhoComItem(ownerId, "10.00").enviar();
    }
}
