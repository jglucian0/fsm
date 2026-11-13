package dev.jgluciano.fsm.modules.orcamento.core.usecase;

import dev.jgluciano.fsm.modules.orcamento.core.model.Orcamento;
import dev.jgluciano.fsm.modules.orcamento.core.ports.OrcamentoRepository;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class DefinirDescontoUseCase {

    private final OrcamentoRepository orcamentoRepository;

    public DefinirDescontoUseCase(OrcamentoRepository orcamentoRepository) {
        this.orcamentoRepository = Objects.requireNonNull(orcamentoRepository, "orcamentoRepository");
    }

    public Orcamento executar(Comando comando) {
        throw new UnsupportedOperationException("Red: ainda não implementado");
    }

    public record Comando(UUID ownerId, UUID orcamentoId, BigDecimal desconto) {

        public Comando {
            Objects.requireNonNull(ownerId, "ownerId");
            Objects.requireNonNull(orcamentoId, "orcamentoId");
            Objects.requireNonNull(desconto, "desconto");
        }
    }
}
