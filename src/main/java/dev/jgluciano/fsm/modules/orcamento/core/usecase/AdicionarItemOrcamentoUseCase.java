package dev.jgluciano.fsm.modules.orcamento.core.usecase;

import dev.jgluciano.fsm.modules.orcamento.core.model.Orcamento;
import dev.jgluciano.fsm.modules.orcamento.core.ports.OrcamentoRepository;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import dev.jgluciano.fsm.modules.orcamento.core.model.TipoItem;
public class AdicionarItemOrcamentoUseCase {

    private final OrcamentoRepository orcamentoRepository;

    public AdicionarItemOrcamentoUseCase(OrcamentoRepository orcamentoRepository) {
        this.orcamentoRepository = Objects.requireNonNull(orcamentoRepository, "orcamentoRepository");
    }

    public Orcamento executar(Comando comando) {
        throw new UnsupportedOperationException("Red: ainda não implementado");
    }

    public record Comando(UUID ownerId, UUID orcamentoId, String descricao, TipoItem tipo, BigDecimal quantidade,
                           BigDecimal valorUnitario) {

        public Comando {
            Objects.requireNonNull(ownerId, "ownerId");
            Objects.requireNonNull(orcamentoId, "orcamentoId");
            Objects.requireNonNull(descricao, "descricao");
            Objects.requireNonNull(tipo, "tipo");
            Objects.requireNonNull(quantidade, "quantidade");
            Objects.requireNonNull(valorUnitario, "valorUnitario");
        }
    }
}
